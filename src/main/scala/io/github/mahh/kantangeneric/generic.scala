package io.github.mahh.kantangeneric

import kantan.csv.CellDecoder
import kantan.csv.DecodeResult
import kantan.csv.RowDecoder

import scala.compiletime.erasedValue
import scala.compiletime.summonInline
import scala.deriving.*

private type SliceSize = 25
private inline def sliceSize = summon[ValueOf[SliceSize]].value

inline private def tupleRowDecoder[T <: Tuple]: RowDecoder[Tuple] =
  inline erasedValue[T] match
    case _: EmptyTuple =>
      RowDecoder.from(_ => DecodeResult.success(EmptyTuple))
    case _: (t *: ts) =>
      RowDecoder.from { row =>
        row.headOption.map { s =>
          for
            a <- summonInline[CellDecoder[t]].decode(s)
            b <- tupleRowDecoder[ts].decode(row.drop(1))
          yield a *: b
        }.getOrElse(DecodeResult.outOfBounds(0))
      }

inline private def slicedTupleRowDecoder[T <: Tuple]: RowDecoder[Tuple] =
  inline erasedValue[T] match
    case _: EmptyTuple =>
      RowDecoder.from(_ => DecodeResult.success(EmptyTuple))
    case _: (t *: ts) =>
      val takeDecoder = tupleRowDecoder[Tuple.Take[t *: ts, SliceSize]]
      val dropDecoder = slicedTupleRowDecoder[Tuple.Drop[t *: ts, SliceSize]]
      RowDecoder.from { row =>
        val (takeRow, dropRow) = row.splitAt(sliceSize)
        for
          a <- takeDecoder.decode(takeRow)
          b <- dropDecoder.decode(dropRow)
        yield a ++ b
      }

inline given derived[T](using p: Mirror.ProductOf[T]): RowDecoder[T] =
  slicedTupleRowDecoder[p.MirroredElemTypes].map(p.fromProduct(_))
