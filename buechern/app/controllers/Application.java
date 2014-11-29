package controllers;

import java.util.ArrayList;

import models.Book;
import models.Model;
import models.User;
import play.*;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {
	
	
	
	
	static Boolean isLogged = false;
	static Boolean dummySet = false;
	
	
	public static Result index() {
    	//load dummy objects 
		if(dummySet == false){
			Model.addDummy();

			dummySet=true;
		}

		return ok(index.render());
	}

	public static Result  profile() {
		if(isLogged == true){

			if(Model.getBookList().isEmpty()){

				return ok(profile.render(Model.getActivUser().getUserBook(),Model.getActivUser(),Model.getActivUser().getMarketBasket()));

			}else{

				return ok(profile.render(Model.getActivUser().getUserBook(),Model.getActivUser(),Model.getActivUser().getMarketBasket()));
			}
		}else{

			return ok(registrierung.render(false));	
		}

		
	}

	public static Result changeUserData(){
		return ok(userDatenAendern.render());
	}

	public static Result verkaufen(){
		
		if(isLogged==true){
			return ok(verkaufen.render());
		}else{
			return ok(registrierung.render(false));
		}
	}
	
	public static Result einkaufen(){
		ArrayList<Book> returnBook = new ArrayList<Book>();
		for(Book book : Model.getBookList()){
			if(book.getStatus()==0){
				returnBook.add(book);
			}
		}
		
		return ok(einkaufen.render(returnBook));
	}
	
	public static Result registrierung(){
		return ok(registrierung.render(false));
	}
	
	public static Result addBook(String Booktitel, 
		String Autor, 
		String Erscheinungsjahr, 
		String ISBN , 
		String Auflage, 
		String Zustand, 
		String Preis, 
		String BoolFestpreis){
		
		AppBookOptions.addBook(Booktitel, Autor, Erscheinungsjahr, ISBN, Auflage, Zustand, Preis, BoolFestpreis);
		
		return ok(profile.render(Model.getActivUser().getUserBook(),Model.getActivUser(),Model.getActivUser().getMarketBasket()));
	}

	
	public static Result deleteBook(int id){
		AppBookOptions.deleteBook(id);
		return ok(profile.render(Model.getActivUser().getUserBook(),Model.getActivUser(),Model.getActivUser().getMarketBasket()));
	}
	
	public static Result addUser(String FirstName,
		String LastName,
		String Email, String EmailRep, 
		String Passwort, String PasswortRep){

		User newUser = new User();
		
				newUser.setFirstName(FirstName);
				newUser.setEmail(Email);
				newUser.setPassword(Passwort);
				Model.getUserList().add(newUser);
				Model.getActivUser().getUserBook().clear();
				Model.getActivUser().getMarketBasket().clear();
				Model.setActivUser(newUser);
				isLogged = true;
				
				return ok(profile.render(Model.getActivUser().getUserBook(),Model.getActivUser(),Model.getActivUser().getMarketBasket()));
	}
	
	public static Result logIn(String benutzername, String passwort){
		
		for(User user : Model.getUserList()){
			
			if(benutzername.equals(user.getFirstName()) && passwort.equals(user.getPassword()) ){
				isLogged = true;
				Model.setActivUser(user);
				Model.getActivUser().getUserBook().clear();
				Model.getActivUser().getMarketBasket().clear();
				for(Book book : Model.getBookList()){
					if(book.getUser().equals(user)){
						Model.getActivUser().getUserBook().add(book);
					}
					if(!(book.getBuyer()==null)){
						
						if(book.getBuyer().equals(user)){
							Model.getActivUser().getMarketBasket().add(book);
						}
					}
				}
			
				return ok(profile.render(Model.getActivUser().getUserBook(),Model.getActivUser(),Model.getActivUser().getMarketBasket()));
			}
		}
		return ok(registrierung.render(false));
	}
	
	public static Result logOut(){
		
		Model.setActivUser(null);
		isLogged = false;
		
		return ok(index.render());
	}
	public static Result buyBook(int id){
			
		if(isLogged==false){
			return redirect("/profile");
		}else{
			
		
			for(Book book : Model.getBookList()){
				if(book.getId()==id){
					for(User user: Model.getUserList()){
						if(user.equals(book.getUser())){
							book.setStatus(1);
							book.setBuyer(Model.getActivUser());
							Model.getActivUser().getMarketBasket().add(book);
						}
					}
				}
			}
		
			//return ok(profile.render(Model.getActivUser().getUserBook(),Model.getActivUser(),Model.getActivUser().getMarketBasket()));
			return redirect("/profile");
		}
	}
	
	public static Result changePass(String oldPass, String newPass ){
			
			if(Model.getActivUser().getPassword().equals(oldPass)){
				Model.getActivUser().setPassword(newPass);
				return ok(profile.render(Model.getActivUser().getUserBook(),Model.getActivUser(),Model.getActivUser().getMarketBasket()));
			}else{
				return ok(userDatenAendern.render());
			}
	}
	
	public static Result changeEmail(String oldEmail, String newEmail ){
		
		if(Model.getActivUser().getEmail().equals(oldEmail)){
			Model.getActivUser().setEmail(newEmail);
			return ok(profile.render(Model.getActivUser().getUserBook(),Model.getActivUser(),Model.getActivUser().getMarketBasket()));
		}else{
			return ok(userDatenAendern.render());
		}
	}
	
}

