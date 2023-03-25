package io.github.mahh.kantantest

import kantan.csv._
import kantan.csv.ops._
import io.github.mahh.kantangeneric.derived

import munit.FunSuite

class GenericRowDecoderSuite extends FunSuite {

  case class CaseClass104(
    a1: Int, b1: Int, c1: Int, d1: Int, e1: Int, f1: Int, g1: Int, h1: Int, i1: Int, j1: Int, k1: Int, l1: Int,
    m1: Int, n1: Int, o1: Int, p1: Int, q1: Int, r1: Int, s1: Int, t1: Int, u1: Int, v1: Int, w1: Int, x1: Int,
    y1: Int, z1: Int,
    a2: Int, b2: Int, c2: Int, d2: Int, e2: Int, f2: Int, g2: Int, h2: Int, i2: Int, j2: Int, k2: Int, l2: Int,
    m2: Int, n2: Int, o2: Int, p2: Int, q2: Int, r2: Int, s2: Int, t2: Int, u2: Int, v2: Int, w2: Int, x2: Int,
    y2: Int, z2: Int,
    a3: Int, b3: Int, c3: Int, d3: Int, e3: Int, f3: Int, g3: Int, h3: Int, i3: Int, j3: Int, k3: Int, l3: Int,
    m3: Int, n3: Int, o3: Int, p3: Int, q3: Int, r3: Int, s3: Int, t3: Int, u3: Int, v3: Int, w3: Int, x3: Int,
    y3: Int, z3: Int,
    a4: Int, b4: Int, c4: Int, d4: Int, e4: Int, f4: Int, g4: Int, h4: Int, i4: Int, j4: Int, k4: Int, l4: Int,
    m4: Int, n4: Int, o4: Int, p4: Int, q4: Int, r4: Int, s4: Int, t4: Int, u4: Int, v4: Int, w4: Int, x4: Int,
    y4: Int, z4: Int
  )


  test("derivation works for case classes with more than 100 fields") {
    val decoder: RowDecoder[CaseClass104] = implicitly
    val decodeResult = decoder.decode((1 to 104).map(_.toString))
    val expected = Right(CaseClass104(
      1, 2, 3, 4, 5, 6, 7, 8, 9,
      10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
      20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
      30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
      40, 41, 42, 43, 44, 45, 46, 47, 48, 49,
      50, 51, 52, 53, 54, 55, 56, 57, 58, 59,
      60, 61, 62, 63, 64, 65, 66, 67, 68, 69,
      70, 71, 72, 73, 74, 75, 76, 77, 78, 79,
      80, 81, 82, 83, 84, 85, 86, 87, 88, 89,
      90, 91, 92, 93, 94, 95, 96, 97, 98, 99,
      100, 101, 102, 103, 104
    ))
    assertEquals(decodeResult, expected)
  }

  test("out of bounds is returned with correct index") {
    val decoder: RowDecoder[CaseClass104] = derived
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
