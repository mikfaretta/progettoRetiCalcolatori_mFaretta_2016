package chat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jndi.ActiveMQInitialContextFactory;

/**
 * Class : JmsChatter Descrizione : Applicazione JMS Publisher/Subscriber
 * 
 * @version 1.0 17/04/2016
 * @author Michelangelo Faretta
 */
public class JmsChatter {

	protected String url = "tcp://localhost:61616";
	
	/* Componenti interfaccia grafica */
	private JFrame frame = null;
	private JButton bClear = null;
	private JButton bSend = null;
	private JTextField tfTx = null;
	private JTextArea taRx = null;

	/** Il nome dell'utente */
	private String userName = null;
	/** La connesione JMS */
	private TopicConnection topicConnection = null;
	/** Il Message sender */
	private TopicPublisher topicPublisher = null;
	/** Il messaggio */
	private TextMessage message = null;

	/**
 * @param args parametri da riga di comando
 */
 public static void main(String[] args) {
 
 if(args.length == 0) {
 try {
	new JmsChatter("jms/ConnectionFactory","jms/topic/MyTopic");
} catch (JMSException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
 }
 else if ( (args.length == 1)) {
 try {
	new JmsChatter(args[0],"MyTopic");
} catch (JMSException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
 }
 else if ( (args.length == 2)) {
 try {
	new JmsChatter(args[0],args[1]);
} catch (JMSException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
 }
 else {
 System.out.println("Usage:[<topic-connection-factory><topic-name>]");
 }
 }

	/**
	 * Costruttore
	 * 
	 * @param topicConnectionFactoryName
	 *            Connection Factory name
	 * @param topicName
	 *            Destination name
	 * @throws JMSException 
	 */
	public JmsChatter(String topicConnectionFactoryName, String topicName) throws JMSException {
		try {
			ActiveMQInitialContextFactory jndiContext = this.getContext();
			System.out.println("JNDI Context[" + jndiContext + "]...");

			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
			TopicConnection connection = (TopicConnection) connectionFactory.createTopicConnection();  
			  
			connection.start();
			// Look up connection ConnectionFactory e detination Topic
			System.out.println("Looking up connection factory [" + topicConnectionFactoryName + "]...");
			Session session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);  

			System.out.println("Looking up topic [" + topicName + "]...");
//			queue = session.createTopic(queueName);
			Topic topic = session.createTopic(topicName);

			System.out.println("topicConnectionFactory [" + topicConnectionFactoryName + "] - topic [" + topic + "]");
			this.go(connectionFactory, topic);

		} catch (NamingException ne) {
			System.out.println(" # NamingException : " + ne.toString());
			ne.printStackTrace();
			System.exit(1);
		}
	}

	/**
 * Configurazione dell'applicazione (publisher e subscriber del Topic)
 * @param topicConnectionFactory l'oggetto Connection Factory
 * @param topic l'oggetto destinazione Topic
 */
 private void go(ActiveMQConnectionFactory topicConnectionFactory,
                   Topic topic){
 
 
 try {
 this.readUserName(); //legge il nome dell'utente da stdin
 
 this.buildGUI(); //costruisce la GUI
 
 // JMS
 this.topicConnection = topicConnectionFactory.createTopicConnection();
 System.out.println("topicConnection ["+topicConnection+"]");
 
 TopicSession topicSession = 
		 topicConnection.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
 System.out.println("topicSession ["+topicSession+"]");

 // Si crea l'oggetto message
 this.message = topicSession.createTextMessage();

 // Parte di publisher
 this.topicPublisher = topicSession.createPublisher(topic);
 System.out.println("topicPublisher ["+topicPublisher+"]");

 // Parte di Subscriber
 TopicSubscriber topicSubscriber = topicSession.createSubscriber(topic,null,true);
 System.out.println("topicSubscriber ["+topicSubscriber+"]");

 System.out.println("Creazione e installazione del listener ...");
 MyTextListener topicListener = new MyTextListener(taRx);
 topicSubscriber.setMessageListener(topicListener);
 topicConnection.start();
 System.out.println("Inizializzazione JMS completata ...");

 } catch (JMSException e) {
	 System.out.println(" # JMSException : " + e.toString());
	 e.printStackTrace();
	 System.exit(0);
 }
}

	/**
	 * Legge da tastiera il nome dell'utente
	 */
	private void readUserName() {
		try {
			BufferedReader msgStream = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Inserisci il tuo nome : ");
			this.userName = msgStream.readLine();
		} catch (IOException ioe) {
			this.userName = "";
		}
	}

	/**
 * Crea e ritorna il JNDI Context
 * @return il JNDI Context creato
 */
 private ActiveMQInitialContextFactory getContext() throws NamingException {
 ActiveMQInitialContextFactory ctx = new ActiveMQInitialContextFactory();
 // Print Context envinroment properties
// java.util.Hashtable ht = ctx.getEnvironment();
// java.util.Enumeration enum1 = ht.keys();
// while(enum1.hasMoreElements()) {
// String str = (String)enum1.nextElement();
// System.out.println("\tProp["+str + "] - value[" + ht.get(str)+"]");
// }
 return ctx;
 }

	/** Costruisce l'interfaccia grafica */
	public void buildGUI() {
		// Costruzione del Frame
		frame = new JFrame("JMS Chat : utente[" + userName + "]");
		frame.getContentPane().setLayout(new BorderLayout());

		// inizializzazione degli oggetti della GUI
		tfTx = new JTextField();
		taRx = new JTextArea();
		taRx.setEditable(false);
		bClear = new JButton("CLEAR");
		bSend = new JButton("SEND");

		// disposizione dei componenti grafici
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(bClear, BorderLayout.WEST);
		p.add(tfTx, BorderLayout.CENTER);
		p.add(bSend, BorderLayout.EAST);

		// il bottone send invia il messaggio contenuto nel TextField
		bSend.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					message.setText("[" + userName + "] " + tfTx.getText());
					topicPublisher.publish(message);
					System.out.println("Message[" + tfTx.getText() + "] sent ...");
				} catch (JMSException jmse) {
					tfTx.setText(jmse.getMessage());
					jmse.printStackTrace();
				}
			}
		});

		// il bottone clear ripulisce il TextField
		bClear.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tfTx.setText("");
			}
		});

		// l'evento di chiusura della finestra genera la chiusura
		// della connessione JMS
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.out.println("windowClosing : JMS clean-up code...");
				if (topicConnection != null) {
					try {
						topicConnection.close();
					} catch (JMSException jmse) {
					}
				}
				System.exit(0);
			}
		});

		frame.getContentPane().add(p, BorderLayout.NORTH);
		JScrollPane sc = new JScrollPane(taRx);
		frame.getContentPane().add(sc, BorderLayout.CENTER);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setSize(screenSize.width / 2, screenSize.height / 2);
		Dimension frameSize = frame.getSize();
		frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

		frame.setVisible(true);
	}
}
