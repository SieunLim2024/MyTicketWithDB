package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.w3c.dom.UserDataHandler;

import main.Main;

public class Cart{

	public static ArrayList<CartItem> cartItem = new ArrayList<>();
	public static int cartCount = 0;
	public static int totalcount = 0;// 구매한 항목 수

	public Cart() {
		super();
	}

	public Cart(ArrayList<CartItem> cartItem, ArrayList<CartItem> paymentItem) {
		super();
		this.cartItem = cartItem;
	}
}