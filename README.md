A demo / proof of concept project showing how the limitations of 
[kantan.csv](https://github.com/nrinaudo/kantan.csv) regarding number of case class parameters
(maximum 22) could be solved in Scala 3.

The code only implements generic derivation for `RowDecoder`s, but the gist of it is:

If it was possible to derive the relevant typeclasses for `HList`s in Scala 2, then it is possible
to derive typeclasses for case classes with > 22 fields in Scala 3.

### With or without shapeless?

There are two implementations of derivation:

1. [using plain scala derivation mechanism](src/main/scala/io/github/mahh/kantangeneric/generic.scala) (see usage in
   [tests](src/test/scala/io/github/mahh/kantantest/GenericRowDecoderSuite.scala))
2. [using shapeless-3](src/main/scala/io/github/mahh/kantangeneric/shapeless/generic.scala) (see usage in
   [tests](src/test/scala/io/github/mahh/kantantest/shapeless/ShapelessGenericRowDecoderSuite.scala))

For product types with more than 27 fields, shapeless-based requires to use the `-Xmax-inlines` compiler parameter
to increase the number of successive inlines (limited to 32 by default).

The non-shapeless variant supports more than 100 fields before hitting that limit.
