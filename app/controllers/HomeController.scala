package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import chatmen.udb.model.User
import chatmen.udb.persistence.default._

@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  case class Add(name:String,email:String,password:String)

  implicit lazy val executionContext = defaultExecutionContext
  // //bodyをtext(文字列)にバインド
  // val signUpForm = User(
  //   mapping(
  //     "id"          -> text,
  //     "name"        -> text,
  //     "email"       -> text,
  //     "phoneNumber" -> text
  //   )(Add.apply)(Add.unapply)
  // )
  // def list()  = Action{
  //   DB.
  // }
  def getAllTweet() =    Action { implicit request: Request[AnyContent] =>
    Ok(views.html.mainMenu())
  }

  def index() =    Action.async { implicit request: Request[AnyContent] =>
    val id = User.Id(1)
    for {
      user <- UserRepository.get(id)
    } yield Ok(s"$user")
  }


  def signin() =    Action { implicit request: Request[AnyContent] =>
    Ok(views.html.signin())
  }

  def complete() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.complete())
  }

  def sendForm() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.signup())
  }

  def mainMenu() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.mainMenu())
  }

  def commit() = Action { implicit req =>
    Ok("compiled")
  }
}
