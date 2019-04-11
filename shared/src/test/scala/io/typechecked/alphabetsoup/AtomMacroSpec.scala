package io.typechecked.alphabetsoup
import macros._
import org.scalatest._
import shapeless.test.illTyped

class AtomMacroSpec extends FlatSpec with Matchers {

	"@Atomic" should "create implicit Atom for an empty case class" in {

		@Atomic case class Foo()

  	implicitly[Atom[Foo]]
	}
	it should "create an implicit Atom for a case class with a single field" in {
		@Atomic case class Foo(bar: String)

  	implicitly[Atom[Foo]]
	}
	it should "create an implicit Atom for a case class with a multiple fields" in {
		@Atomic case class Foo(bar: String, baz: Int, bam: Float)

  	implicitly[Atom[Foo]] 
	}
	it should "create an implicit Atom for a case class with a multiple fields and function defintions inside" in {
		@Atomic case class Foo(bar: String, baz: Int, bam: Boolean) {
			def getBar: String = bar
			def getBaz: Int = baz
		}

  	implicitly[Atom[Foo]] 

  	val foo = Foo("bar", 7, true)

  	foo.getBar shouldBe "bar"
  	foo.getBaz shouldBe 7
  	foo.bam shouldBe true
	}
	it should "place implicit inside companion object if one already exists" in {
		@Atomic case class Foo()

		object Foo {
			val iAmHere: String = "Yeah Baby!"
		}

		implicitly[Atom[Foo]]

		Foo.iAmHere shouldBe "Yeah Baby!"

	}
	it should "place implicit inside companion object if one already exists for a case class with multiple fields" in {
		@Atomic case class Foo(bar: String, baz: Int, bam: Boolean) {

			def giveMeBam: Boolean = bam
		}

		object Foo {
			val iAmHere: String = "Yeah Baby!"
		}

		implicitly[Atom[Foo]]

		val foo = Foo("plumbus", 42, false)

		Foo.iAmHere shouldBe "Yeah Baby!"
		foo.bar shouldBe "plumbus"
		foo.baz shouldBe 42
		foo.giveMeBam shouldBe false

		"Foo.Fooatom" should compile

	}
	it should "blow up with ambiguous implicits if an Atom is already defined" in {
		@Atomic case class Foo()

		object Foo {
			implicit val atom: Atom[Foo] = Atom[Foo]
		}

		illTyped("implicitly[Atom[Foo]]",".*ambiguous implicit values.*")
	}
	it should "blow up if something is defined inside companion object with same name as generated one" in {
		illTyped("""
			@Atomic case class Foo()

			object Foo {
				val Fooatom = "I am the real Fooatom!!!"
			}
		""",".*Fooatom is already defined as value Fooatom.*")
	}
	it should "create an implicit Atom for a Class" in {
		@Atomic class Foo()

		implicitly[Atom[Foo]]
	}
	it should "create an implicit Atom for a protected sealed abstract protected Class" in {

		implicitly[Atom[PrivateFoo]]
	}
	it should "create an implicit Atom for a Trait" in {
		@Atomic trait Foo

		implicitly[Atom[Foo]]
	}
	it should "create an implicit Atom for a Trait with existing functions" in {
		@Atomic trait Foo {
			def bar: String = "bar"
		}

		implicitly[Atom[Foo]]

		val foo = new Foo {}	
		foo.bar shouldBe "bar"
	}
	it should "create an implicit Atom for nested classes" in {
		class A {
			@Atomic class B
			object B 
			implicitly[Atom[B]] 
		}
	}
	it should "create an implicit Atom when companion object is far away" in {
		@Atomic class A

		trait B
		@Atomic trait C

		object A

		implicitly[Atom[A]]
		implicitly[Atom[C]]

	}
	it should "" in {
		def foo = {
			@Atomic class A

			implicitly[Atom[A]]
		}
	}
}

@Atomic protected sealed abstract class PrivateFoo(implicit impl: DummyImplicit)
