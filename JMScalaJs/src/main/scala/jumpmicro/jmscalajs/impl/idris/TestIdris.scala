package universe.microservice.jmscalajs.impl.idris

import universe.microservice.microservicescalajs.impl.idris.JBar

//: ----------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: ----------------------------------------------------------------------------------

// Proof that Idris code can run from Scala, refer to Main.idr
object TestIdris {
  def test() = {
    println(JBar.pythag(5))
  }
}
