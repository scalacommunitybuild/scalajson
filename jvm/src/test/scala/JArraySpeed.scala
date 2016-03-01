import org.scalameter.api._

object JArraySpeed extends Bench.ForkedTime {
  val sizes: Gen[Int] = Gen.range("size")(300000, 1500000, 300000)

  val chars: Gen[Array[Char]] = for {
    size <- sizes
  } yield size.toString.toCharArray

}
