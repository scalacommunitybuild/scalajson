package specs

import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, WordSpec}

trait Spec extends WordSpec with PropertyChecks with Matchers
