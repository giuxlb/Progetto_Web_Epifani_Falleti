package it.polimi.web.project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import it.polimi.web.project.beans.BankAccount;
import it.polimi.web.project.beans.Transfer;

public class TransferDao {

	private Connection con;

	public TransferDao(Connection c) {
		this.con = c;
	}

	public int createTransfer(int destUserID, int destBankAccountID, int userID, int bankAccountID, int amount,
			String causal) throws SQLException {
		BankAccountDao cdao = new BankAccountDao(con);

		List<BankAccount> counts = cdao.findBankAccountsByUser(destUserID);

		boolean verified1 = false;

		if (counts == null)
			return 0;

		for (int i = 0; i < counts.size(); i++) {
			if (counts.get(i).getID() == destBankAccountID) // abbiamo trovato il conto
			{
				verified1 = true;
				break;
			}
		} // qui ho controllato se il conto sotto quel contoID è effettivamente dello user
			// id e se il saldo di quel conto, permette un trasferimento di quell'importo

		if (!verified1)
			return 0;

		BankAccount bankAccount = cdao.findBankAccountByID(bankAccountID);
		if (bankAccount.getBalance() < amount)
			return 1;

		// se verified è ancora false, ritorniamo false

		int amountDest = 0 - amount;
		// qui verified sarà true, quindi possiamo modificare sia l'importo del conto di
		// destinazione, sia quello di quello di origine origine e aggiungere il
		// trasferimento al dbms
		

		java.util.Date d = new java.util.Date();

		String query = "INSERT INTO esercizio4.trasferimento (DestContoID,ContoID,causale,importo,data) VALUES(?,?,?,?,?)";
		try (PreparedStatement statement = con.prepareStatement(query)) {
			statement.setInt(1, destBankAccountID);
			statement.setInt(2, bankAccountID);
			statement.setString(3, causal);
			statement.setInt(4, amount);
			statement.setDate(5, new java.sql.Date(d.getTime()));
			statement.execute();

		}
		
		cdao.changeBalance(amount, destBankAccountID);
		cdao.changeBalance(amountDest, bankAccountID);

		return 2;
	}

	public List<Transfer> findTransfersByBankAccount(int bankAccountID) throws SQLException {
		List<Transfer> transfers = new ArrayList<Transfer>();
		String query = "SELECT * FROM esercizio4.trasferimento where ContoID = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, bankAccountID);
			try (ResultSet result = pstatement.executeQuery()) {
				if (result.isBeforeFirst()){
					while (result.next()) {
						Transfer t = new Transfer();
						t.setTransferID(result.getInt("trasferimentoID"));
						System.out.println(t.getTransferID());
						t.setCausal(result.getString("causale"));
						t.setBankAccountID(bankAccountID);
						t.setData(result.getDate("data"));
						t.setAmount(result.getInt("importo"));
						t.setDestBankAccountId(result.getInt("DestContoID"));
						transfers.add(t);
					}
				}
			}
		}
		query = "SELECT * FROM esercizio4.trasferimento where DestContoID = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, bankAccountID);
			try (ResultSet result = pstatement.executeQuery()) {
				if (result.isBeforeFirst()){
					while (result.next()) {
						Transfer t = new Transfer();
						t.setTransferID(result.getInt("trasferimentoID"));
						t.setCausal(result.getString("causale"));
						t.setBankAccountID(bankAccountID);
						t.setData(result.getDate("data"));
						t.setAmount(result.getInt("importo"));
						t.setDestBankAccountId(result.getInt("DestContoID"));
						transfers.add(t);
					}
				}
			}
		}
		transfers.sort(new Comparator<Transfer>() {
			@Override
			public int compare(Transfer t1, Transfer t2) {
				if (t1.getData().compareTo(t2.getData()) == 0) {
					return t2.getTransferID() - t1.getTransferID();
				} else if (t1.getData().compareTo(t2.getData()) > 0)
					return -1;
				return 1;
			}
		});
		System.out.println(transfers.size());
		return transfers;
	}

}
