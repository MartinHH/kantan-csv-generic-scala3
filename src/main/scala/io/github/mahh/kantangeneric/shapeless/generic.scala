package io.github.mahh.kantangeneric.shapeless

import kantan.csv.CellDecoder
import kantan.csv.DecodeError
import kantan.csv.DecodeResult
import kantan.csv.RowDecoder
import shapeless3.deriving.*

given rowDecoderGen[A] (using inst: K0.ProductInstances[RowDecoder, A]): RowDecoder[A] with

  // accumulator for unfold:
  // - the index (for DecodeError.OutOfBounds)
  // - a decode error (to propagate the reason for failure)
  // - the remainder of the row
  type Acc = (Int, Option[DecodeError], Seq[String])

  def decode(row: Seq[String]): DecodeResult[A] =
    val ((i, errOpt, _), aOpt) = inst.unfold[Acc]((0, None, row)) {
      [t] =>
        (acc: Acc, rd: RowDecoder[t]) =>
          val (i, _, r) = acc
          if (r.isEmpty)
            val newAcc: Acc = (i, Some(DecodeError.OutOfBounds(i)), r)
            newAcc -> None
          else
            rd.decode(Seq(r.head)).fold(
              e => (i, Some(e), r.tail) -> Option.empty[t],
              t => (i + 1, None, r.tail) -> Some(t)
            )
    }
    aOpt.fold(DecodeResult.failure(errOpt.getOrElse(DecodeError.OutOfBounds(i))))(DecodeResult.apply)

inline given derived[A](using K0.ProductInstances[RowDecoder, A]): RowDecoder[A] = rowDecoderGen
