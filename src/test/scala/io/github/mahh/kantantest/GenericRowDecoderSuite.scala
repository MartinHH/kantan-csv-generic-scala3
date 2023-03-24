package io.github.mahh.kantantest

import kantan.csv._
import kantan.csv.ops._
import io.github.mahh.kantangeneric.derived

import munit.FunSuite

class GenericRowDecoderSuite extends FunSuite {

  test("derivation works for case classes with more than 22 members") {
    // note that derivation for a case class with 31 parameters would fail with:
    //   Maximal number of successive inlines(32) exceeded,
    //   Maybe this is caused by a recursive inline method?
    //   You can use -Xmax-inlines to change the limit.
    // That is indeed cause by recursive inlining in io.github.mahh.kantangeneric.tupleRowDecoder
    // and can be solved by incrementing that compiler-setting (to at least number_of_case_class_fields + 2).
    case class CaseClass30(
      a1: Int, b1: Int, c1: Int, d1: Int, e1: Int, f1: Int, g1: Int, h1: Int, i1: Int, j1: Int, k1: Int, l1: Int,
      m1: Int, n1: Int, o1: Int, p1: Int, q1: Int, r1: Int, s1: Int, t1: Int, u1: Int, v1: Int, w1: Int, x1: Int,
      y1: Int, z1: Int,
      a2: Int, b2: Int, c2: Int, d2: Int
    )
    val decoder: RowDecoder[CaseClass30] = implicitly
    assertEquals(
      decoder.decode((1 to 30).map(_.toString)),
      Right(CaseClass30(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
        17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30))
    )
  }
}
