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
case class PostTweet(uid: Long, text: String)
case class Follow(targetid: Long)

@Singleton
class TweetController @Inject()(cc: ControllerComponents) extends AbstractController(cc)with play.api.i18n.I18nSupport{

  implicit lazy val executionContext = defaultExecutionContext
  //tweet全部のデータをtext(文字列)にバインド
  val tweetForm = Form(
    mapping(
      "uid"           -> optional(longNumber),
      "text"         -> text,
      "update"       -> localDateTime,
      "createdAt"    -> localDateTime
    )(Post.apply)(Post.unapply(_))
  )

  //tweetの内容をtext(文字列)にバインド
  val tweetTextForm = Form(
    mapping(
      "text"         -> text
    )(TweetContent.apply)(TweetContent.unapply(_))
  )

  //Tweetを投稿するためにフォームの内容をバインド
  val postTweetForm = Form(
    mapping(
      "uid"          -> longNumber,
      "text"         -> text
    )(PostTweet.apply)(PostTweet.unapply(_))
  )

  //followの内容をlongにバインド
  val followForm = Form(
    mapping(
      "targetid"         -> longNumber
    )(Follow.apply)(Follow.unapply(_))
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
  def postTweet() = Action.async{implicit req =>
    val post     = postTweetForm.bindFromRequest.get
    val addTweet = Tweet.WithNoId(User.Id(post.uid), post.text)
    for{
      newTwwet    <- TweetRepository.add(addTweet)
    }yield Ok(newTwwet.toString)
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

  //フォロー機能(UserEachRelationを作成)
  def follow(uid:Long)  = Action.async{implicit req =>
    //val followByForm = followForm.bindFromRequest.get
    val follow       = UserEachRelation.WithNoId(User.Id(1), User.Id(uid))
    for{
      addFollow <- UserEachRelationRepository.add(follow)
    }yield Ok(addFollow.toString)
  }

  //アンフォロー機能
  def unfollow(uid:Long)  = Action.async{implicit req =>
    for{
      removeFollow <- UserEachRelationRepository.remove(UserEachRelation.Id(uid))
    }yield Ok(removeFollow.toString)
  }

  //Tweet投稿画面の表示
  def showPostForm() =    Action { implicit req: Request[AnyContent] =>
    Ok(views.html.postTweet(postTweetForm))
  }

  //ユーザ一覧画面の表示
  def showAllUser() =    Action { implicit req: Request[AnyContent] =>
    Ok(views.html.ff())
  }
}


// //アンフォロー機能
// def unfollow(uid:Long)  = Action.async{implicit req =>
//   val unFollowByForm    = followForm.bindFromRequest.get
//   val follow            = UserEachRelation.WithNoId(User.Id(uid.get), User.Id(followByForm.targetid))
//   for{
//     unfollow <- UserEachRelationRepository.add(unfollow)
//   }yield Ok(addFollow.toString)
// }
