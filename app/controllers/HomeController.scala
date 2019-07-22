package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import chatmen.udb.model.User
import chatmen.udb.persistence.default._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{AbstractController, Action, Controller, ControllerComponents}

case class UserAdd(name:String,email:String,password:String)

@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc)with play.api.i18n.I18nSupport{

  implicit lazy val executionContext = defaultExecutionContext
  //signinのデータをtext(文字列)にバインド
  val userForm = Form(
    mapping(
      "name"        -> text,
      "email"       -> text,
      "password"    -> text
    )(UserAdd.apply)(UserAdd.unapply)
  )

  val hoge = Form("name"        -> text)
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

  // def signin() =    Action { implicit request: Request[AnyContent] =>
  //   val a = "hoge"
  //   Ok(views.html.signin(userInfoForm))
  // }

  def complete() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.complete())
  }

  // def sendForm() = Action { implicit request: Request[AnyContent] =>
  //   Ok(views.html.signup(userInfoForm))
  // }

  def mainMenu() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.mainMenu())
  }

  def commit() = Action { implicit req =>
    userForm.bindFromRequest.fold(
      error =>{
        BadRequest("error")
      },
      success => {
        val name = success.name
        // val user = User(success.name, success.email, success.passwoed)
        Ok(s"$name")
      }
    )
  }
}
