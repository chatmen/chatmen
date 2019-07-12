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

case class UserInfoAdd(name:String,email:String,password:String)
case class UserInfo(name: String, email: String, phoneNumber: Option[String], password: String)
case class UserAuth(email: String, password: String)

@Singleton
class UserController @Inject()(cc: ControllerComponents) extends AbstractController(cc)with play.api.i18n.I18nSupport{

  implicit lazy val executionContext = defaultExecutionContext
  //signinのデータをtext(文字列)にバインド
  val userInfoForm = Form(
    mapping(
      "name"        -> text,
      "email"       -> text,
      "password"    -> text
    )(UserInfoAdd.apply)(UserInfoAdd.unapply)
  )


  def commitUserInfo() = Action { implicit req =>
    userInfoForm.bindFromRequest.fold(
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

  // def createUser() = Action{ implicit req =>
  //   userInfoForm.bindFromRequest.fold(
  //     error => 
  //   )
  // }
}
