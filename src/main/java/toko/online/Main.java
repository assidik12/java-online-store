package toko.online;


import java.io.IOException;
import java.util.Date;
import java.util.Scanner;
import java.util.UUID;

import toko.online.model.transaction;
import toko.online.model.user;
import toko.online.view.ProductView;
import toko.online.view.TransctionView;
import toko.online.view.authView;


public class Main  {
    public static void main(String[] args) throws IOException {
        
        Scanner scanner = new Scanner(System.in);
        System.out.println("=".repeat(20) );
        System.out.println("selamat datang di toko online");
        System.out.println("=".repeat(20));
        System.out.println("1. daftar");
        System.out.println("2. lihat barang");
        System.out.println("3. transaction");
        System.out.println("pilih menu: ");
        int menu = scanner.nextInt();
        if (menu == 1) {
            System.out.println("=".repeat(20) );
            System.out.print("masukkan username: ");
            String username = scanner.next();
            System.out.print("masukkan password: ");
            String password = scanner.next();
            System.out.print("masukkan email: ");
            String email = scanner.next();
            authView auth = new authView();
            System.out.println("=".repeat(20) );
            user user = new user(username, password,email);
            auth.RegisterView(user);
            scanner.close();
        } else if (menu == 2) {
            System.out.println("=".repeat(20) );
            ProductView product = new ProductView();
            product.getProducts();
            System.out.println("apakah anda ingin membeli barang? (y/n)");
            String answer = scanner.next();
            if (answer.equals("y")) {
                System.out.println("=".repeat(20) );
                System.out.println("masukan id product yang ingin dibeli: ");
                int id_product = scanner.nextInt();
                System.out.println("=".repeat(20) );
                System.out.println("masukan qty: ");
                int qty = scanner.nextInt();
                System.out.println("=".repeat(20) );
                System.out.println("masukan uang anda : ");
                int total_amount  = scanner.nextInt();
                System.out.println("masukan email anda : ");
                String email = scanner.next();
                UUID id_transaction = UUID.randomUUID();
                Date date = new Date();

                transaction transaction = new transaction(email, id_product, id_transaction.toString(),qty,0,total_amount, date, true);
                TransctionView transctionView = new TransctionView();
                transctionView.buyProduct(transaction);
            }
            System.out.println("=".repeat(20) );
            
            scanner.close();
        } else if (menu == 3) {
            System.out.println("=".repeat(20) );
            System.out.println("masukan email: ");
            String email = scanner.next();
            System.out.println("=".repeat(20) );
            TransctionView transaction = new TransctionView();
            transaction.viewTransactions(email);
            scanner.close();
        }
    }
 
    
 
}
