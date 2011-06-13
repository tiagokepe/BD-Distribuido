package com.ufpr.gdd.view;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.ufpr.gdd.Manipulacao;
import com.ufpr.gdd.ManipulacaoException;
import com.ufpr.gdd.Principal;

public class View 
{
	public void run(String args[]){
		Manipulacao manipulator = null;
		int options;
		Scanner input = new Scanner(System.in);
		System.out.println("================== P2P XFX SYTEM 1.0.1 ==================");
		System.out.print("\nInicializando sistema ... ");

		try {
			manipulator = Principal.mainP2P(args);
			System.out.println("done");

			System.out.println("\nOpções: ");
			System.out.println("1) Inserir");
			System.out.println("2) Buscar");
			System.out.println("3) Sair");
		} catch (Exception e) {
			System.out.println("Sistema inicializado com erro. "
					+ e.getMessage());
		}

		options = input.nextInt();
		while (options != 3){
			switch (options){
			case 1:
				try {
					String nameFile, title, subject, description, date;
					DateFormat formatter;
					Date formatedDate;
					
					final JFileChooser fc = new JFileChooser();

					fc.showDialog(null, "Selecionar");
					nameFile = fc.getSelectedFile().getAbsolutePath();

					title = JOptionPane.showInputDialog(
							null, "Título: ");
					title=( title.equals(""))?null:title;

					subject = JOptionPane.showInputDialog(
							null, "Assunto: ");
					subject=( subject.equals(""))?null:subject;

					description=JOptionPane.showInputDialog(
							null, "Descrição: ");
					description=( description.equals(""))?null:description;
					
					date =JOptionPane.showInputDialog(null, "Data (DD/MM/YYYY):");
					date=(date.equals(""))?null:date;
					if ( date == null )
						formatedDate = null; 
					else {
						try {
							formatter= new SimpleDateFormat("dd/MM/yyyy");
							formatedDate = (Date) formatter.parse(date);
						} catch ( ParseException e) {
							formatedDate=null;
						}
					}
					manipulator.armazenar(nameFile, title, subject, description, formatedDate);
				} catch (Exception e){
					e.printStackTrace();
				}
				break;
			case 2:
				try {
				    ButtonGroup group = new ButtonGroup();
				    JRadioButton buscaData = new JRadioButton("Data");
				    JRadioButton buscaRegular = new JRadioButton("Regular");
				    JTextField name = new JTextField(30);
				    
				    Object[] array = {
				            new JLabel("Selecione o tipo de busca desejado:"),
				            buscaRegular,
				            buscaData
				        };

			        String termo = JOptionPane.showInputDialog(null, array, "Busca", 
				                                                JOptionPane.OK_CANCEL_OPTION);
			        if ( buscaData.isSelected() ) {
						DateFormat formatter;
						Date formatedDate;
						try {
							formatter= new SimpleDateFormat("dd/MM/yyyy");
							formatedDate = (Date) formatter.parse(termo);
							termo = formatedDate.toString();
						} catch ( ParseException e) {
							e.printStackTrace();
						}	
			        }					
					manipulator.buscar(termo);
				} catch( ManipulacaoException e ) {
					System.out.println("Erro na busca: "+e.getMessage());
				}
			}
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("\n ============================================================= \n");
			System.out.println("Opções: ");
			System.out.println("1) Inserir");
			System.out.println("2) Buscar");
			System.out.println("3) Sair");

			options = input.nextInt();	
		}


		System.out.println("==========================================================");
	}
}
