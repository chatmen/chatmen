package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import chatmen.udb.model.User
import chatmen.udb.persistence.default._
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called nwhen the application receives a `GET` request with
   * a path of `/`.
    */
  
  implicit lazy val executionContext = defaultExecutionContext
  // def list()  = Action{
  //   DB.
  // }
  def index() =    Action.async { implicit request: Request[AnyContent] =>
    val id = User.Id(1)
    for {
      user <- UserRepository.get(id)
    } yield Ok(s"$user")
  }

  def login() =    Action { implicit request: Request[AnyContent] =>
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

}
