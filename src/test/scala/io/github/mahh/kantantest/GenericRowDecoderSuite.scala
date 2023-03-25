package io.github.mahh.kantantest

import kantan.csv._
import kantan.csv.ops._
import io.github.mahh.kantangeneric.derived

import munit.FunSuite

class GenericRowDecoderSuite extends FunSuite {

  case class CaseClass52(
    a1: Int, b1: Int, c1: Int, d1: Int, e1: Int, f1: Int, g1: Int, h1: Int, i1: Int, j1: Int, k1: Int, l1: Int,
    m1: Int, n1: Int, o1: Int, p1: Int, q1: Int, r1: Int, s1: Int, t1: Int, u1: Int, v1: Int, w1: Int, x1: Int,
    y1: Int, z1: Int,
    a2: Int, b2: Int, c2: Int, d2: Int, e2: Int, f2: Int, g2: Int, h2: Int, i2: Int, j2: Int, k2: Int, l2: Int,
    m2: Int, n2: Int, o2: Int, p2: Int, q2: Int, r2: Int, s2: Int, t2: Int, u2: Int, v2: Int, w2: Int, x2: Int,
    y2: Int, z2: Int
  )


  test("derivation works for case classes with more than 32 fields") {
    val decoder: RowDecoder[CaseClass52] = implicitly
    val decodeResult = decoder.decode((1 to 52).map(_.toString))
    val expected = Right(CaseClass52(
      1, 2, 3, 4, 5, 6, 7, 8, 9,
      10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
      20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
      30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
      40, 41, 42, 43, 44, 45, 46, 47, 48, 49,
      50, 51, 52
    ))
    assertEquals(decodeResult, expected)
  }

  test("out of bounds is returned with correct index") {
    val decoder: RowDecoder[CaseClass52] = derived
    val oob = 42
    val decodeResult = decoder.decode((0 until oob).map(_.toString))
    val expected = DecodeResult.outOfBounds(oob)
    assertEquals(decodeResult, expected)
  }

  test("error of a member-decoder is returned correctly") {
    sealed trait Foo
    val fooError = DecodeResult.typeError("Foo")

    given CellDecoder[Foo] = CellDecoder.from(_ => fooError)

    case class Bar(a: Int, b: Foo, c: String)
    val decoder: RowDecoder[Bar] = derived

    val decodeResult = decoder.decode(Seq("1", "foo", "some string"))
    assertEquals(decodeResult, fooError)
  }
}
