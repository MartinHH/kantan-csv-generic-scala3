package io.github.mahh.kantantest.shapeless

import io.github.mahh.kantangeneric.shapeless.derived
import kantan.csv.*
import kantan.csv.ops.*
import munit.FunSuite

class ShapelessGenericRowDecoderSuite extends FunSuite {

  case class MaxFieldsCaseClass(
    a1: Int, b1: Int, c1: Int, d1: Int, e1: Int, f1: Int, g1: Int, h1: Int, i1: Int, j1: Int, k1: Int, l1: Int,
    m1: Int, n1: Int, o1: Int, p1: Int, q1: Int, r1: Int, s1: Int, t1: Int, u1: Int, v1: Int, w1: Int, x1: Int,
    y1: Int, z1: Int,
    a2: Int /*, b2: Int */
  )


  test("derivation works for case classes with up to 27 fields") {
    // (28 fields would hit the maxinline barrier of 32)
    val decoder: RowDecoder[MaxFieldsCaseClass] = implicitly
    val decodeResult = decoder.decode((1 to 27).map(_.toString))
    val expected = Right(MaxFieldsCaseClass(
      1, 2, 3, 4, 5, 6, 7, 8, 9,
      10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
      20, 21, 22, 23, 24, 25, 26, 27 /*, 28 */
    ))
    assertEquals(decodeResult, expected)
  }

  test("out of bounds is returned with correct index") {
    val decoder: RowDecoder[MaxFieldsCaseClass] = implicitly
    val oob = 3
    val decodeResult = decoder.decode((0 until oob).map(_.toString))
    val expected = DecodeResult.outOfBounds(oob)
    assertEquals(decodeResult, expected)
  }

  test("error of a member-decoder is returned correctly") {
    sealed trait Foo
    val fooError = DecodeResult.typeError("Foo")

    given CellDecoder[Foo] = CellDecoder.from(_ => fooError)

    case class Bar(a: Int, b: Foo, c: String)
    val decoder: RowDecoder[Bar] = implicitly

    val decodeResult = decoder.decode(Seq("1", "foo", "some string"))
    assertEquals(decodeResult, fooError)
  }
}
