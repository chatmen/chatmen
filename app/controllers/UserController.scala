package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import chatmen.udb.model._
import chatmen.udb.persistence.default._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{AbstractController, Action, Controller, ControllerComponents}

case class UserInfoAdd(name:String,email:String,password:String)

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

  //認証のデータをバインド
  val userAuthForm = Form(
    mapping(
      "email"       -> text,
      "password"    -> text
    )(userAuth.apply)(userAuth.unapply)
  )

  //ユーザのフォーム情報を表示
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

  //会員登録
  def registerUser() = Action{implicit req =>
    userRegiForm.bindFromRequest.fold(
      error => {
        BadRequest("error")
      },
      success => {
        val user = UserInfo(success.name, success.email, success.password)
        val hashFromPassword = UserPassword.hash(success.password)
        val addUser          = User.WithNoId(success.name, success.email)
        for{
          id             <- UserRepository.add(addUser)
          passByWithNoId <- UserPassword.WithNoId(User.Id(id), hashPass)
          pass           <- UserPasswordRepository.update(passByWithNoId)
        }yield Ok(id)
      }
    )
  }

  //フォームを受け取り、認証
  def checkUserByForm() = Action{implicit req =>
    userAuthForm.bindFromRequest.fold(
      error => {
        BadRequest("error")
      },
      success => {
        val form    = (success.email, success.password)
        for{
          user   <- UserRepository.getEmail(form._1)
          pass   <- UserPasswordRepository.get(user.get.id)
          result = UserPassword.verify(form._2 ,pass.get.v.hash)
        }yield result match {
          case true  => Ok(s"${user.get.v}")
          case false => BadRequest("user not exist")
        }
      }
    )
  }
  //認証機能: メアドから、同一idのパスワードを取得し、ハッシュ化し、パスワードの検証
  def checkUser() = Action.async{implicit req =>
    val form = ("e@s.net","hoge")
    for{
      user   <- UserRepository.getEmail(form._1)
      pass   <- UserPasswordRepository.get(user.get.id)
      result = UserPassword.verify(form._2 ,pass.get.v.hash)
    }yield result match {
      case true  => Ok(s"${user.get.v}")
      case false => BadRequest("user not exist")
    }
  }
}


//val a = User.WithNoId("hoge", "e@s.net")
//val b = User(Some(User.Id(1)), "hoge", "a@net")

// def createUser() = Action{ implicit req =>
//   userInfoForm.bindFromRequest.fold(
//     error =>
//   )
// }

//userid <- UserRepository.add(a)
//   userPass = UserPassword(User.Id(4), "hoge")
//pass  <- UserPasswordRepository.get(User.Id(9))
// pass2  <- UserPasswordRepository.update(userPass)
