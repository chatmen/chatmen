package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import chatmen.udb.model.User
import chatmen.core.model.Tweet
import chatmen.core.persistence.default._
import chatmen.udb.persistence.default._
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
        Ok(post.toString)
      }
    )
  }
  //   //タイムライン機能(自分がフォローしてる人のツイート情報出力)
  // def post(uid: User.Id) = Action.async{implicit req =>
  //   tweetForm.bindFromRequest.fold(
  //     error => {
  //       BadRequest(errorMessage)
  //     },
  //     postRequest => {
  //       val post = Post(postRequest.uid, TweetRepository.get(TweetRepository.filterByUserId(uid )), LocalDateTime.now, LocalDateTime.now)
  //       //TweetRepository.add(post)
  //       Ok(post.toString)
  //     }
  //   )
  // }

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

  //uidのフォローしてる人全員のTweetの全情報をGet
  def gets(uid: Option[Long]) = Action.async { implicit req =>
    for{
      userid  <- UserEachRelationRepository.getFollowsOfUser(User.Id(uid.get))
      tweet <- TweetRepository.filterByUserIds(userid)
     }yield Ok(tweet.toString)
  }

  //uidからTweetをGET
  def getsTweetId(uid: Option[Long]) = Action.async { implicit req =>
    for{
      tweet <- TweetRepository.filterByUserId((User.Id(uid.get)))
    }yield Ok(s"${tweet.map(x => x.v.text)}")
  }
}
