package chat;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * Class : MyTextListener Descrizione : la classe listener della destinazione
 * topic
 */
class MyTextListener implements MessageListener {

	private javax.swing.JTextArea textArea = null;

	/**
	 * Costruttore
	 * 
	 * @param ta
	 *            TextArea visualizza contenuto dei messaggi ricevuti
	 */
	public MyTextListener(javax.swing.JTextArea ta) {
		this.textArea = ta;
	}

	/**
	 * Effettua il cast del messaggio e ne visualizza il contenuto
	 * 
	 * @param message
	 *            il messaggio ricevuto
	 */
	public void onMessage(Message message) {

		TextMessage msg = null;

		try {
			if (message instanceof TextMessage) {
				msg = (TextMessage) message;
				// stampo a video
				System.out.println(new java.util.Date().toString() + "Ricevuto messaggio : " + msg.getText());
				// stampo nella TextArea
				this.textArea.append(
						new java.util.Date().toString() + ":" + msg.getText() + System.getProperty("line.separator"));
			} else {
				System.out.println(
						"TextListener.onMessage : Message of             wrong type: " + message.getClass().getName());
			}
		} catch (JMSException e) {
			System.out
					.println(" # TextListener.onMessage :             JMSException in onMessage(): " + e.getMessage());
			e.printStackTrace();
		} catch (Throwable te) {
			System.out.println(" # TextListener.onMessage :             Exception in onMessage():" + te.getMessage());
			te.printStackTrace();
		}
	}
}
