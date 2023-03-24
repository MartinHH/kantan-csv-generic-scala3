package io.github.mahh.kantangeneric

import kantan.csv.CellDecoder
import kantan.csv.DecodeResult
import kantan.csv.RowDecoder

import scala.compiletime.erasedValue
import scala.compiletime.summonInline
import scala.deriving.*

inline private def tupleRowDecoder[T <: Tuple]: RowDecoder[Tuple] =
  inline erasedValue[T] match
    case _: EmptyTuple =>
      RowDecoder.from(_ => DecodeResult.success(EmptyTuple))
    case _: (t *: ts) =>
      RowDecoder.from { row =>
        row.headOption.map { s =>
          for {
            a <- summonInline[CellDecoder[t]].decode(s)
            b <- tupleRowDecoder[ts].decode(row.drop(1))
          } yield a *: b
        }.getOrElse(DecodeResult.outOfBounds(0))
      }

inline given derived[T](using p: Mirror.ProductOf[T]): RowDecoder[T] =
  tupleRowDecoder[p.MirroredElemTypes].map(p.fromProduct(_))
