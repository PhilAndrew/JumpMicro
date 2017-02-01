package jumpmicro.jmscalajs.impl.idris

//: -------------------------------------------------------------------------------------
//: Copyright © 2017 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: Released under the MIT License, refer to the project website for licence information.
//: -------------------------------------------------------------------------------------

// Proof that Idris code can run from Scala, refer to Main.idr
object TestIdris {
  def test() = {
    println(JBar.pythag(5))
  }
}
