package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import chatmen.udb.model.User
import chatmen.core.model.Tweet
import chatmen.core.persistence.default._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{AbstractController, Action, Controller, ControllerComponents}
import java.time.LocalDateTime
import views.html.defaultpages.error
import play.api.data._

case class Post(text: String, updatedAt: LocalDateTime, createdAt: LocalDateTime)

@Singleton
class TweetController @Inject()(cc: ControllerComponents) extends AbstractController(cc)with play.api.i18n.I18nSupport{

  implicit lazy val executionContext = defaultExecutionContext
  //signinのデータをtext(文字列)にバインド
  val tweetForm = Form(
    mapping(
      "text"         -> text,
      "update"       -> localDateTime,
      "createdAt"    -> localDateTime
    )(Post.apply)(Post.unapply)
  )

  def getAllTweet() =    Action.async { implicit request: Request[AnyContent] =>
    val id = Tweet.Id(1)
    for {
      tweet <- TweetRepository.get(id)
    }yield Ok(s"$tweet")
  }

  def post = Action{implicit req =>
    tweetForm.bindFromRequest.fold(
      error => {
        val errorMessage = "error"
        BadRequest(errorMessage)
      },
      postRequest => {
        val post = Post(postRequest.text, LocalDateTime.now, LocalDateTime.now)
        //TweetRepository.add(post)
        Ok(s"$post")
      }
    )
  }


}
