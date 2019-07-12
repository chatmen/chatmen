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

case class TweetInfo(: String, password: String)

@Singleton
class UserController @Inject()(cc: ControllerComponents) extends AbstractController(cc)with play.api.i18n.I18nSupport{

  implicit lazy val executionContext = defaultExecutionContext
  //signinのデータをtext(文字列)にバインド
  val tweetForm = Form(
    mapping(
      "id"           -> optional(long),
      "uid"          -> optional(long),
      "text"         -> text,
      "updatedAt"    -> localDate,
      "createdAt"    -> localDate
    )(TweetInfo.apply)(TweetInfo.unapply)
  )

  def getAllTweet() =    Action { implicit request: Request[AnyContent] =>
    Ok(TweetRepository.get(id: Id) )
  }
}
