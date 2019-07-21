package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import chatmen.udb.model._
import chatmen.core.model._
import chatmen.udb.persistence.default._
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
case class TweetContent(text: String)

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

  //signinのデータをtext(文字列)にバインド
  val tweetTextForm = Form(
    mapping(
      "text"         -> text
    )(TweetContent.apply)(TweetContent.unapply(_))
  )
  val errorMessage = "error"

  //uidのTweet全情報を表示する
  def getTweetDate() = Action.async { implicit req =>
    val id = Tweet.Id(1)
    for {
      tweet <- TweetRepository.get(id)
    }yield Ok(s"$tweet")
  }

  //Tweetのフォームの内容を表示する
  def post = Action{implicit req =>
    tweetForm.bindFromRequest.fold(
      error => {
        BadRequest(errorMessage)
      },
      postRequest => {
        val post = Post(postRequest.uid, postRequest.text, LocalDateTime.now, LocalDateTime.now)
        //TweetRepository.add(post)
        Ok(post.toString)
      }
    )
  }

  //Tweetのフォーム情報を表示
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

  //Tweetの投稿
  def postTweet() = Action{implicit req =>
    tweetTextForm.bindFromRequest.fold(
      error => {
        BadRequest(errorMessage)
      },
      postRequest => {
        //val post = tweetForm.bindFromRequest.get
        val tweet = TweetContent(postRequest.text)
        //TweetRepository.add(posts)
        Ok(s"$tweet")
        Redirect("/")
      }
    )
  }

  //フォローしてる人全員のTweetの全情報を取得
  def getTweetByFollowId(uid: Option[Long]) = Action.async { implicit req =>
    for{
      userid  <- UserEachRelationRepository.getFollowsOfUser(User.Id(uid.get))
      tweet <- TweetRepository.filterByUserIds(userid)
    }yield Ok(s"${tweet.map(x => x.v.text)}")
  }

  //フォローしてる人全員のTweet内容を取得
  def getTweetDateByFollowId(uid: Option[Long]) = Action.async { implicit req =>
    for{
      userid  <- UserEachRelationRepository.getFollowsOfUser(User.Id(uid.get))
      tweet <- TweetRepository.filterByUserIds(userid)
    }yield Ok(tweet.toString)
  }

  //特定ユーザのTweet内容をGET
  def getTweetDateByUserId(uid: Option[Long]) = Action.async { implicit req =>
    for{
      tweet <- TweetRepository.filterByUserId((User.Id(uid.get)))
    }yield Ok(s"${tweet.map(x => x.v.text)}")
  }
}
