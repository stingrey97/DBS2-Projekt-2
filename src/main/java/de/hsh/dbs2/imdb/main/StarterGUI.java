package de.hsh.dbs2.imdb.main;

import javax.swing.SwingUtilities;

import de.hsh.dbs2.imdb.gui.SearchMovieDialog;
import de.hsh.dbs2.imdb.gui.SearchMovieDialogCallback;
import de.hsh.dbs2.imdb.util.HibernateConnection;

public class StarterGUI {

	/**
	 * @param args
	 * @throws Throwable 
	 */
	public static void main(String[] args) throws Throwable {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new StarterGUI().run();
			}
		});

		// Startup Hibernate
		HibernateConnection.get();

	}
	
	public void run() {
		SearchMovieDialogCallback callback = new SearchMovieDialogCallback();
		SearchMovieDialog sd = new SearchMovieDialog(callback);
		sd.setVisible(true);
	}

}
