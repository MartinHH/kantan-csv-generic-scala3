package io.github.mahh.kantangeneric

import kantan.csv.CellDecoder
import kantan.csv.DecodeResult
import kantan.csv.RowDecoder

import scala.compiletime.erasedValue
import scala.compiletime.ops
import scala.compiletime.summonInline
import scala.deriving.*

private type SliceSize = 25
private inline def sliceSize = summon[ValueOf[SliceSize]].value

inline private def tupleRowDecoder[T <: Tuple, I <: Int]: RowDecoder[Tuple] =
  inline erasedValue[T] match
    case _: EmptyTuple =>
      RowDecoder.from(_ => DecodeResult.success(EmptyTuple))
    case _: (t *: ts) =>
      RowDecoder.from { row =>
        row.headOption.map { s =>
          for
            a <- summonInline[CellDecoder[t]].decode(s)
            b <- tupleRowDecoder[ts, ops.int.S[I]].decode(row.drop(1))
          yield a *: b
        }.getOrElse(DecodeResult.outOfBounds(summonInline[ValueOf[I]].value))
      }

inline private def slicedTupleRowDecoder[T <: Tuple, I <: Int]: RowDecoder[Tuple] =
  inline erasedValue[T] match
    case _: EmptyTuple =>
      RowDecoder.from(_ => DecodeResult.success(EmptyTuple))
    case _: (t *: ts) =>
      val takeDecoder = tupleRowDecoder[Tuple.Take[t *: ts, SliceSize], I]
      val dropDecoder = slicedTupleRowDecoder[Tuple.Drop[t *: ts, SliceSize], ops.int.+[I, SliceSize]]
      RowDecoder.from { row =>
        val (takeRow, dropRow) = row.splitAt(sliceSize)
        for
          a <- takeDecoder.decode(takeRow)
          b <- dropDecoder.decode(dropRow)
        yield a ++ b
      }

inline given derived[T](using p: Mirror.ProductOf[T]): RowDecoder[T] =
  slicedTupleRowDecoder[p.MirroredElemTypes, 0].map(p.fromProduct(_))
