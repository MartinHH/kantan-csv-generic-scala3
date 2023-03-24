A demo / proof of concept project showing how the limitations of 
[kantan.csv](https://github.com/nrinaudo/kantan.csv) regarding number of case class parameters
(maximum 22) could be solved in Scala 3.

The code only implements generic derivation for `RowDecoder`s and tests this with a `RowDecoder`
for a case class with 30 fields, but the gist of it is:

If it was possible to derive the relevant typeclasses for `HList`s in Scala 2, then it is possible
to derive typeclasses for case classes with > 22 fields in Scala 3.