package com.ufpr.gdd.view;

import java.util.Date;
import java.util.Scanner;

import com.ufpr.gdd.Manipulacao;
import com.ufpr.gdd.ManipulacaoException;
import com.ufpr.gdd.Principal;

public class View 
{
	public void run(String args[]){
		int options;
		Scanner input = new Scanner(System.in);
		System.out.println("================== P2P XFX SYTEM 1.0.1 ==================");
		System.out.print("\nInicializando sistema ... ");
		
		try {
			Manipulacao manipulator = Principal.mainP2P(args);
			System.out.println("done");
			
			System.out.println("\nOpções: ");
			System.out.println("1) Inserir");
			System.out.println("2) Buscar");
			System.out.println("3) Sair");
			
			options = input.nextInt();
			while (options != 3){
				switch (options){
					case 1:
						try {
							String nameFile, title, subject, description;
						
							System.out.println("Caminho: ");
							nameFile = input.next();
							System.out.println("Título: ");
							title = input.next();
							System.out.println("Subject: ");
							subject = input.next();
							System.out.println("Description: ");
							description = input.next();
						
							manipulator.armazenar(nameFile, title, subject, description, new Date());
						} catch (Exception e){
							System.out.println("Erro: "+e.getMessage());
						}
					break;
					case 2:
						try {
							System.out.println("Digite o termo a ser usado na pesquisa:");
							manipulator.buscar(input.next());
						} catch( ManipulacaoException e ) {
							System.out.println("Erro na busca: "+e.getMessage());
						}
				}
				
				System.out.println("\nOpções: ");
				System.out.println("1) Inserir");
				System.out.println("2) Buscar");
				System.out.println("3) Sair");
				
				options = input.nextInt();	
			}
		} catch (Exception e) {
			System.out.println("Sistema inicializado com erro. "
					+ e.getMessage());
		}
		
		System.out.println("==========================================================");
	}
}
