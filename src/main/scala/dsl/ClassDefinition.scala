package dsl

import dsl.AccessModifiers.{AccessModifiers, PUBLIC}

import scala.collection.{immutable, mutable}
import scala.collection.mutable.ListBuffer

abstract class ClassDefinitionOption
case class Constructor(commands: Command*) extends ClassDefinitionOption
//case class Field(fieldName: String) extends ClassDefinitionOption
case class Field(fieldName: String, accessModifier: AccessModifiers = PUBLIC) extends ClassDefinitionOption
case class Method(name: String, commands: Command*) extends ClassDefinitionOption
case class Extends(name: String) extends ClassDefinitionOption

class ClassDefinition(val name: String, val options: ClassDefinitionOption*) {
  private val fields = mutable.Map.empty[String, Field_] // why is this a map? maybe we can store the type of field
  private val methods = mutable.Map.empty[String, MethodDefinition]
  private val constructor = mutable.ListBuffer.empty[Command]
  private val parentClass = mutable.Set.empty[String]


  for (option <- options) {
    option match {
      case Constructor(commands @ _*) => {
        this.constructor.addAll(commands.toList)
      }
      case Method(name: String, commands @ _*) => {
        this.methods.addOne(name, new MethodDefinition(name, commands.toList))
      }
      case Field(name: String, accessModifier: AccessModifiers) => {
        this.fields.addOne(name, new Field_(name, null, accessModifier))
      }


      // handling this outside for now
      // case Extends(name: String) => {
        //this.parentClass.addOne(name)
      //}
      case Extends(name) => {
        // just here to placate a match error since we're handling extends outside
      }
    }
  }

  def getName: String = this.name
  def getConstructor: immutable.List[Command] = this.constructor.toList

  def setParentClass(parentClassName: String): Unit = {
    this.parentClass.addOne(parentClassName)
  }
  def hasParentClass(): Boolean = {
    this.parentClass.size == 1
  }
  def hasMethod(methodName: String): Boolean = this.methods.contains(methodName)

  def hasField(fieldName: String): Boolean = this.fields.contains(fieldName)

  def getMethodDefinition(methodName: String): MethodDefinition = {
    if (hasMethod(methodName)) {
      return this.methods(methodName)
    }
    throw new Exception(s"Method $methodName not found in class $name")
  }

  def getParentClassName(): String = {
    if (this.parentClass.isEmpty) {
      return null
    }
    return this.parentClass.toList.head
  }

  def getFieldAccessModifier(fieldName: String): AccessModifiers = {
    if (this.fields.contains(fieldName)) { // checking only on this class, not parent classes
      return this.fields(fieldName).getAccessModifier()
    } else {
      throw new Exception()
    }
  }

  def getFieldInfo(): List[Field_] = {
    return this.fields.toList.map[Field_](x => x._2)
  }
}
