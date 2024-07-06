object Invariance {
  class ABC[A] // invariant
}
object Covariance1 {

  class ABC[A]

  abstract class Animal {
    def name: String
  }

  case class Dog(name: String) extends Animal

  case class Cat(name: String, colour: String) extends Animal

//  class ImmutableBox[+A](val content: A)
//  val catbox: ImmutableBox[Cat] = ImmutableBox[Cat](Cat("milton", "black"))
//  val animalBox2: ImmutableBox[Animal] = catbox // now this compiles

  class ImmutableBox[+A](val content: A)
  val catbox                           = ImmutableBox(Cat("milton", "black")) // the type of catbox automatically becomes ImmutableBox[Cat]
  val animalBox2: ImmutableBox[Animal] = catbox

// We say that ImmutableBox is covariant in A, and this is indicated by the + before the A.
// More formally, that gives us the following relationship: given some class Cov[+T],
// then if X is a subtype of Y, Cov[X] is a subtype of Cov[Y]. This allows us to make very useful and
// intuitive subtyping relationships using generics.

  abstract class Serializer[-A] {

    def serialize(a: A): String

    val animalSerializer: Serializer[Animal] = new Serializer[Animal]() {
      def serialize(animal: Animal): String = s"""{ "name": "${animal.name}" }"""

    }

    val catSerializer: Serializer[Cat] = animalSerializer
    catSerializer.serialize(Cat("tony", "orange"))

// We say that Serializer is contravariant in A, and this is indicated by the - before the A. A more general serializer
// is a subtype of a more specific serializer.
// More formally, that gives us the reverse relationship: given some class Contra[-T],
// then if Cat is a subtype of Animal, Contra[Animal] is a subtype of Contra[Cat].

//  abstract class Animal:
//    def name: String
//  case class Dog(name: String) extends Animal
//  case class Cat(name: String) extends Animal

    // 1 If `List` is covariant (`List[+A]`), we can also pass a `List[Dog]` or `List[Cat]` to `printCount`, increasing the method's reusability.
    def printCount(animalList: List[Animal]) = println(animalList.length)
    printCount(List(Dog("tom"), Dog("kate")))

    // 2. Type Safety Variance annotations enforce type safety by ensuring that you can't mistakenly put the wrong type into a data structure.
    class Container[+A](val element: A) {

      def abc: String                  = ???
      val animalBox: Container[Animal] = new Container[Dog](Dog("tom")) // Compiles only if Container is covariant: Container[+A]

    }

    // 3. API Design Designing a collection API that allows easy manipulation of data:
    trait Queue[+T] {
      def enqueue[U >: T](x: U): Queue[U] = ???
      // This allows for greater flexibility when using the `Queue`
    }

  }

}

object Covariance2 {

  abstract class Animal {
    def name: String
  }

  case class Dog(name: String) extends Animal
  case class Cat(name: String) extends Animal

  class Container[+A <: Animal](val element: A) {

    def get: A = element // A method to showcase polymorphism using covariance

    def describe: String =
      s"This container holds a ${element.getClass.getSimpleName} named ${element.name}"

  }

  val dogContainer                                       = new Container[Dog](Dog("Rex"))
  val catContainer                                       = new Container[Cat](Cat("Whiskers"))
  def printContainer(container: Container[Animal]): Unit = println(container.describe)

  printContainer(dogContainer)
  printContainer(catContainer)

}
object Contravariance {

  abstract class Animal {

    def sound: String = ???
    case class Dog(override val sound: String = "Woof") extends Animal
    case class Cat(override val sound: String = "Meow") extends Animal

    class Printer[-A] {

      def print(a: A): String =
        s"Printing a ${a.getClass.getSimpleName}. It says: ${a.asInstanceOf[Animal].sound}"

    }

    val animalPrinter: Printer[Animal]                    = new Printer[Animal]
    def printSound(printer: Printer[Dog], dog: Dog): Unit = println(printer.print(dog))

    printSound(animalPrinter, Dog())

  }

  object Contravariance2 {

    trait Function[-T, +R] {
      def apply(input: T): R
    }

    abstract class Animal {
      def sound: String
    }

    case class Dog(sound: String = "Woof") extends Animal
    case class Cat(sound: String = "Meow") extends Animal

    abstract class Sound {

      def asString: String

      case class DetailedSound(private val s: String) extends Sound {
        def asString: String = s"Sound is: $s"
      }

      class AnimalSoundFunction extends Function[Animal, Sound] {
        def apply(animal: Animal): Sound = DetailedSound(animal.sound)
      }

      def useFunction(func: Function[Dog, Sound], dog: Dog): Sound = func.apply(dog)

      val animalSoundFunc = new AnimalSoundFunction()
      val result: Sound   = useFunction(animalSoundFunc, Dog())

      println(s"The sound is: ${result.asString}")

    }

  }

}
//You're right that the code you provided works without explicitly declaring covariance for `ImmutableBox`. The reason it works is due to Scala's support for polymorphism and subtyping.
//
//In the code:
//
//```scala
//class ImmutableBox[A](val content: A)
//val catbox: ImmutableBox[Animal] = ImmutableBox(Cat("milton", "black"))
//val animalBox2: ImmutableBox[Animal] = catbox
//```
//
//Here's what happens:
//
//1. The `ImmutableBox` class is defined with a type parameter `A` and a constructor that takes a value of type `A`.
//
//2. The `catbox` value is declared as an `ImmutableBox[Animal]`, but it is initialized with an instance of `Cat` (`ImmutableBox(Cat("milton", "black"))`). This is allowed because `Cat` is a subtype of `Animal`, and Scala's type system allows assigning an instance of a subtype to a variable of its supertype.
//
//3. The `animalBox2` value is declared as an `ImmutableBox[Animal]` and is assigned the value of `catbox`. This assignment is allowed because `catbox` is also of type `ImmutableBox[Animal]`.
//
//In this case, covariance is not explicitly required because the assignments are compatible due to the subtyping relationship between `Cat` and `Animal`.
//
//However, covariance becomes necessary when you want to establish a subtyping relationship between parameterized types based on the subtyping relationship of their type parameters. Consider the following example:
//
//```scala
//class ImmutableBox[A](val content: A)
//val catbox: ImmutableBox[Cat] = ImmutableBox(Cat("milton", "black"))
//val animalBox2: ImmutableBox[Animal] = catbox // Compilation error: type mismatch
//```
//
//In this case, `catbox` is of type `ImmutableBox[Cat]`, and we're trying to assign it to `animalBox2` of type `ImmutableBox[Animal]`. Without covariance, the compiler will raise a type mismatch error because `ImmutableBox[Cat]` is not considered a subtype of `ImmutableBox[Animal]`.
//
//By declaring `ImmutableBox` as covariant with `[+A]`, you establish the subtyping relationship between `ImmutableBox` instances based on the subtyping relationship of their type parameters. This allows the assignment of `ImmutableBox[Cat]` to `ImmutableBox[Animal]` to be valid.
//
//Covariance is particularly useful when designing generic classes or traits that should preserve the subtyping relationship of their type parameters. It allows for more flexible and reusable code by enabling polymorphism and subtyping with parameterized types.

//S
//why is the following possible? 'class ImmutableBox[A](val content: A); val catbox: ImmutableBox[Animal] = ImmutableBox(Cat("milton", "black"))'

//The code `val catbox: ImmutableBox[Animal] = ImmutableBox(Cat("milton", "black"))` is possible due to Scala's support for polymorphism and subtyping.
//
//In this code:
//
//1. The `ImmutableBox` class is defined with a type parameter `A` and a constructor that takes a value of type `A`.
//
//2. `Cat` is a subtype of `Animal`, which means that an instance of `Cat` can be used wherever an instance of `Animal` is expected.
//
//3. The `catbox` value is declared as an `ImmutableBox[Animal]`, indicating that it can hold an instance of `Animal` or any of its subtypes.
//
//4. When creating the `ImmutableBox` instance `ImmutableBox(Cat("milton", "black"))`, we are passing an instance of `Cat` to the constructor.
//
//5. Since `Cat` is a subtype of `Animal`, and the constructor of `ImmutableBox` expects a value of type `A` (which is `Animal` in this case), the constructor call is valid.
//
//6. The resulting `ImmutableBox` instance, which contains a `Cat`, is then assigned to `catbox`, which is of type `ImmutableBox[Animal]`.
//
//This assignment is allowed because an `ImmutableBox` instance that contains a `Cat` (a subtype of `Animal`) is still considered an `ImmutableBox[Animal]` due to polymorphism. The type system allows the assignment of an instance with a more specific type (`Cat`) to a variable with a more general type (`Animal`).
//
//In other words, since `Cat` is a subtype of `Animal`, an `ImmutableBox` containing a `Cat` can be treated as an `ImmutableBox` containing an `Animal`. This is known as "upcasting" or "widening" the type.
//
//This polymorphic behavior allows for more flexible and reusable code. You can create an `ImmutableBox` with a specific subtype of `Animal` and still treat it as an `ImmutableBox` of `Animal` when needed.
//
//It's important to note that this behavior is possible even without declaring `ImmutableBox` as covariant (`[+A]`). However, covariance becomes necessary when you want to establish a subtyping relationship between `ImmutableBox` instances based on the subtyping relationship of their type parameters, as explained in the previous response.

// S
// Show come "upcasting" or "widening" the type is possible without covariance?

//"Upcasting" or "widening" the type is possible without covariance because it is a fundamental feature of polymorphism and subtyping in object-oriented programming, independent of variance annotations.
//
//In Scala, when a class extends another class or implements a trait, it establishes an "is-a" relationship. This means that an instance of the subclass can be treated as an instance of its superclass or the trait it implements. This is the basis for polymorphism and subtyping.
//
//Consider the following example:
//
//```scala
//abstract class Animal
//case class Cat(name: String) extends Animal
//```
//
//In this code, `Cat` is a subclass of `Animal`. This means that an instance of `Cat` can be used wherever an instance of `Animal` is expected. You can assign a `Cat` instance to a variable of type `Animal`:
//
//```scala
//val cat: Cat = Cat("Whiskers")
//val animal: Animal = cat // Upcasting or widening
//```
//
//Here, `cat` is of type `Cat`, and it is being assigned to `animal` of type `Animal`. This is allowed because `Cat` is a subtype of `Animal`. The type system permits the assignment of a more specific type (`Cat`) to a variable of a more general type (`Animal`). This is "upcasting" or "widening" the type.
//
//The same principle applies when using generic classes without covariance. Consider the `ImmutableBox` example:
//
//```scala
//class ImmutableBox[A](val content: A)
//val catBox: ImmutableBox[Cat] = ImmutableBox(Cat("Whiskers"))
//val animalBox: ImmutableBox[Animal] = catBox // Upcasting or widening
//```
//
//In this case, `catBox` is of type `ImmutableBox[Cat]`, and it is being assigned to `animalBox` of type `ImmutableBox[Animal]`. This assignment is allowed because `Cat` is a subtype of `Animal`, and the type system allows the assignment of a more specific type (`ImmutableBox[Cat]`) to a variable of a more general type (`ImmutableBox[Animal]`).
//
//Covariance, on the other hand, is a way to establish a subtyping relationship between parameterized types based on the subtyping relationship of their type parameters. It allows you to treat an `ImmutableBox[Cat]` as a subtype of `ImmutableBox[Animal]`, enabling polymorphism and subtyping at the level of the parameterized types themselves.
//
//Without covariance, you can still perform "upcasting" or "widening" of individual instances, but you cannot establish a subtyping relationship between the parameterized types themselves. Covariance extends the subtyping relationship to the parameterized types, providing additional flexibility and type safety when working with generic classes and traits.

//S
// the following doesn't work without covariance :
//"""
//class ImmutableBox[A](val content: A) val catBox: ImmutableBox[Cat] = ImmutableBox(Cat("Whiskers")) val animalBox: ImmutableBox[Animal] = catBox // Upcasting or widening
//"""

// You are correct. The code you provided:
//
//```scala
//class ImmutableBox[A](val content: A)
//val catBox: ImmutableBox[Cat] = ImmutableBox(Cat("Whiskers"))
//val animalBox: ImmutableBox[Animal] = catBox // Upcasting or widening
//```
//
//will indeed result in a compilation error without covariance. The error message would be similar to:
//
//```
//type mismatch;
// found   : ImmutableBox[Cat]
// required: ImmutableBox[Animal]
//```
//
//The reason for this error is that, by default, generic classes in Scala are invariant in their type parameters. This means that `ImmutableBox[Cat]` and `ImmutableBox[Animal]` are treated as unrelated types, even though `Cat` is a subtype of `Animal`.
//
//Without covariance, the type system does not allow the assignment of `catBox` (of type `ImmutableBox[Cat]`) to `animalBox` (of type `ImmutableBox[Animal]`), because it does not consider `ImmutableBox[Cat]` to be a subtype of `ImmutableBox[Animal]`.
//
//To make this code work, you need to declare `ImmutableBox` as covariant in its type parameter `A` by using the `+` symbol:
//
//```scala
//class ImmutableBox[+A](val content: A)
//val catBox: ImmutableBox[Cat] = ImmutableBox(Cat("Whiskers"))
//val animalBox: ImmutableBox[Animal] = catBox // Upcasting or widening
//```
//
//By adding the `+` symbol before the type parameter `A`, you are indicating that `ImmutableBox` is covariant in `A`. This establishes a subtyping relationship between `ImmutableBox` instances based on the subtyping relationship of their type parameters.
//
//With covariance, `ImmutableBox[Cat]` is considered a subtype of `ImmutableBox[Animal]`, allowing the assignment of `catBox` to `animalBox` to be valid.
//
//Covariance is particularly useful when you want to enable polymorphism and subtyping for generic classes or traits, allowing instances with more specific types to be used in places where instances with more general types are expected.
//
//It's important to note that covariance has certain restrictions to ensure type safety. For example, a covariant type parameter can only appear in covariant positions (such as the return type of methods) and cannot appear in contravariant positions (such as the parameter types of methods) within the class or trait.

//S
// when would i need ImmutableBox[Cat] rather than ImmutableBox[Animal]?
//val catBox: ImmutableBox[Cat] = ImmutableBox(Cat("Whiskers"))

//You would need to use `ImmutableBox[Cat]` rather than `ImmutableBox[Animal]` in situations where you specifically require an `ImmutableBox` that holds a `Cat` instance and you want to access `Cat`-specific properties or methods.
//
//Consider the following scenario:
//
//```scala
//abstract class Animal {
//  def name: String
//}
//
//case class Cat(name: String, favoriteFood: String) extends Animal
//case class Dog(name: String, breed: String) extends Animal
//
//class ImmutableBox[+A](val content: A) {
//  def getContent: A = content
//}
//```
//
//In this example, both `Cat` and `Dog` are subtypes of `Animal`, but they have additional specific properties. `Cat` has a `favoriteFood` property, and `Dog` has a `breed` property.
//
//Now, let's say you want to create an `ImmutableBox` specifically for a `Cat` instance and access its `favoriteFood` property:
//
//```scala
//val catBox: ImmutableBox[Cat] = ImmutableBox(Cat("Whiskers", "Tuna"))
//val catFavoriteFood: String = catBox.getContent.favoriteFood
//```
//
//In this case, using `ImmutableBox[Cat]` allows you to directly access the `favoriteFood` property of the `Cat` instance stored inside the box. The type system guarantees that `catBox.getContent` returns a `Cat` instance, so you can safely access `Cat`-specific properties and methods.
//
//On the other hand, if you were to use `ImmutableBox[Animal]`, you would lose the specific type information about the `Cat` instance:
//
//```scala
//val animalBox: ImmutableBox[Animal] = ImmutableBox(Cat("Whiskers", "Tuna"))
//val favoriteFood: String = animalBox.getContent.favoriteFood // Compilation error
//```
//
//In this case, `animalBox.getContent` returns an `Animal` instance, which doesn't have the `favoriteFood` property. The compilation error occurs because the type system doesn't know that the `Animal` inside the box is specifically a `Cat`.
//
//Using `ImmutableBox[Cat]` allows you to preserve the specific type information and enables you to access `Cat`-specific properties and methods directly. It provides a stronger type guarantee and allows for more precise operations on the stored instance.
//
//However, if you don't need to access `Cat`-specific properties or methods and only want to treat the stored instance as an `Animal`, using `ImmutableBox[Animal]` would be sufficient. It allows for more flexibility and polymorphism, as you can store any subtype of `Animal` in the box.
//
//In summary, you would choose `ImmutableBox[Cat]` when you specifically need to work with `Cat` instances and access their specific properties or methods, while `ImmutableBox[Animal]` is more general and allows for storing and working with any subtype of `Animal`.

//S
//I have the following article :
//
//Can i not just use Invariant and do the following? It looks simpler :
//
//scala
//
//
//Copy code
//abstract class Serializer[A]:
//  def serialize(a: A): String
//
//val animalSerializer: Serializer[Animal] = new Serializer[Animal]():
//  def serialize(animal: Animal): String = s"""{ "name": "${animal.name}" }"""
//
//animalSerializer.serialize(Cat("Felix"))

//Yes, you can certainly use invariance and achieve the same functionality as the example with contravariance. Using invariance can indeed be simpler in some cases.
//
//In the example you provided:
//
//```scala
//abstract class Serializer[A]:
//  def serialize(a: A): String
//
//val animalSerializer: Serializer[Animal] = new Serializer[Animal]():
//  def serialize(animal: Animal): String = s"""{ "name": "${animal.name}" }"""
//
//animalSerializer.serialize(Cat("Felix"))
//```
//
//Here, `Serializer` is defined as an invariant class, meaning that `Serializer[Animal]` and `Serializer[Cat]` are not related by subtyping. However, you can still create an instance of `Serializer[Animal]` and use it to serialize a `Cat` instance, as shown in the last line.
//
//This works because `Cat` is a subtype of `Animal`, and the `serialize` method of `animalSerializer` accepts an `Animal` parameter. When you pass a `Cat` instance to `animalSerializer.serialize`, it is implicitly upcast to `Animal`, which is a valid argument for the method.
//
//Using invariance in this way can be simpler and more straightforward, especially if you don't need the extra flexibility provided by contravariance.
//
//However, contravariance has its advantages in certain scenarios. Consider the following example:
//
//```scala
//def serializeList(list: List[Animal], serializer: Serializer[Animal]): List[String] =
//  list.map(serializer.serialize)
//
//val cats: List[Cat] = List(Cat("Whiskers"), Cat("Tom"))
//val catSerializer: Serializer[Cat] = new Serializer[Cat]():
//  def serialize(cat: Cat): String = s"""{ "name": "${cat.name}", "genus": "Felis" }"""
//
//serializeList(cats, catSerializer)
//```
//
//In this case, we have a `serializeList` method that takes a `List[Animal]` and a `Serializer[Animal]`, and serializes each animal in the list using the provided serializer.
//
//If `Serializer` was invariant, we wouldn't be able to pass a `Serializer[Cat]` as the second argument to `serializeList`, even though it's perfectly valid to use a `Cat` serializer to serialize a list of `Animal` instances (since every `Cat` is an `Animal`).
//
//By making `Serializer` contravariant, we establish the subtyping relationship `Serializer[Animal] <: Serializer[Cat]`, allowing us to pass a more specific serializer (`Serializer[Cat]`) where a more general one (`Serializer[Animal]`) is expected.
//
//In summary, while using invariance can be simpler in some cases, contravariance provides additional flexibility when you need to use a more specific type in place of a more general one. The choice between invariance and contravariance depends on the specific requirements and design of your program.
