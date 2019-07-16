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
import play.api.data.format.Formats._

case class Post(uid:Option[Long], text: String, updatedAt: LocalDateTime, createdAt: LocalDateTime)

@Singleton
class TweetController @Inject()(cc: ControllerComponents) extends AbstractController(cc)with play.api.i18n.I18nSupport{

  implicit lazy val executionContext = defaultExecutionContext
  //signinのデータをtext(文字列)にバインド
  val tweetForm = Form(
    mapping(
      "uid"           -> optional(longNumber),
      "text"         -> text,
      "update"       -> localDateTime,
      "createdAt"    -> localDateTime
    )(Post.apply)(Post.unapply(_))
  )
  val errorMessage = "error"

  def getAllTweet() =    Action.async { implicit request: Request[AnyContent] =>
    val id = Tweet.Id(1)
    for {
      tweet <- TweetRepository.get(id)
    }yield Ok(s"$tweet")
  }

  def post = Action{implicit req =>
    tweetForm.bindFromRequest.fold(
      error => {
        BadRequest(errorMessage)
      },
      postRequest => {
        val post = Post(postRequest.uid, postRequest.text, LocalDateTime.now, LocalDateTime.now)
        //TweetRepository.add(post)
        Ok(s"$post")
      }
    )
  }

  def add() = Action { implicit request =>
    tweetForm.bindFromRequest.fold(
      error => {
        BadRequest(errorMessage)
      },
      postRequest => {
        //val post = tweetForm.bindFromRequest.get
        val posts = Post(postRequest.uid, postRequest.text, LocalDateTime.now, LocalDateTime.now)
        //TweetRepository.add(posts)
        //Ok(views.html.index(TweetRepository.add(post), form))
        Ok(s"$posts")
        Redirect("/")
      }
    )
  }

  // def gets(uid: option[User.Id]) = action { implicit req =>
  //   date match {
  //     case some(date) => {
  //       ok(views.html.index((postrepository.find(localdate.parse(date))), form))

  //     }
  //     //特定のユーザのtweetを表示
  //     case none => ok(views.html.index((postrepository.find(localdate.now)), form))
  //   }
  // }
}
