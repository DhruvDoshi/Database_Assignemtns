import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Queue;

public class Tranaction_initial implements Runnable{
	public static final String dbase = "jdbc:mysql://1122.170.144.248:3306/csci5408_a2";
	public static final String userdb = "try";
	public static final String passworddb = "check123";
	public int thread_num;
	public String str_city;
	public static Queue<Integer> queue_1= makenew();
	public static void main(String args[])
	{
		
		Tranaction_initial ti1=new Tranaction_initial(1, "thr1 city");
		Tranaction_initial ti2=new Tranaction_initial(2, "thr2 city");
		Tranaction_initial ti3=new Tranaction_initial(3, "thr3 city");
		Thread thr1=new Thread(ti1);
		Thread thr2=new Thread(ti2);
		Thread thr3=new Thread(ti3);
		thr1.start();
		thr2.start();
		thr3.start();    	
	}
	@Override
	public void run() {
		try {
			BufferedWriter buff_writer = new BufferedWriter(new FileWriter("logfile1.txt",true));
			Connection con_connect=DriverManager.getConnection(dbase,userdb,passworddb);
			con_connect.setAutoCommit(false);
			buff_writer.write("Thread started"+thread_num+"\n");
			buff_writer.close();
			while(!queue_1.isEmpty())
			{
				System.buff_writer.println(queue_1);
				if(!queue_1.contains(thread_num))
					break;
				if(queue_1.peek()!=queue_1.peek().equals(thread_num) && null)
				int i=0;
				{
					try{
						if(i=2){
							Statement statemnt1=con_connect.createStatement();
							statemnt1.executeUpdate(committrans());
							buff_writer = new BufferedWriter(new FileWriter("logfile1.txt",true));
							buff_writer.write("thread commit "+thread_num+"\n");
							buff_writer.close();
							con_connect.commit();
							statemnt1.close();
							con_connect.close();
							queue_1.remove();	
						}
						else if(i==1){
							buff_writer = new BufferedWriter(new FileWriter("logfile1.txt",true));
							buff_writer.write("thread updating "+thread_num+"\n");
							buff_writer.close();
							PreparedStatement statmnt2=con_connect.prepareStatement(updateCity());
							statmnt2.setString(1, str_city);
							statmnt2.executeUpdate();
							statmnt2.close();
							queue_1.remove();
						}
						else if(i==0){

							Statement statmnt3=con_connect.createStatement();
							ResultSet resset=statmnt3.executeQuery(readcustomers());
							buff_writer = new BufferedWriter(new FileWriter("logfile1.txt",true));
							buff_writer.write("thread reading "+thread_num+"\n");
							buff_writer.close();
							while(resset.next())
							{
								buff_writer = new BufferedWriter(new FileWriter("logfile1.txt",true));
								buff_writer.write(resset.getString("customer_id")+"\n"+
								resset.getString("customer_unique_id")+"\n"+
								resset.getString("customer_city")+"\n"+
								resset.getString("customer_zip_code_prefix")+"\n"+
								resset.getString("customer_state")+"\n");
								buff_writer.close();
							}
							resset.close();
							statmnt3.close();
							queue_1.remove();
						}
						else{
							
							return ;
						}
					}
					catch (SQLException e) {
						e.printStackTrace();
					}
					i++;
				}
			}
			buff_writer.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public static String readcustomers()
	{
		//reading from the db
		return "select * from cust where customer_zip_code_prefix=1151";
		
	}
	public static String updateCity()
	{
		//update the database with the query
		return "update cust set customer_city = ? where  customer_zip_code_prefix=1151";
		
	}
	public static String committrans()
	{
		//returning commit
		return "commit";
	}
	public static Queue<Integer> makenew()
	{
		Queue<Integer> queue_1=new LinkedList<Integer>();
		queue_1.add(1);//thr1 read
		queue_1.add(2);//thr2 read
		queue_1.add(1);//thr1 write
		queue_1.add(3);//thr3 read
		queue_1.add(2);//thr2 write
		queue_1.add(3);//thr3 write
		queue_1.add(1);//thr1 commit
		queue_1.add(3);//thr3 commit
		queue_1.add(2);//thr2 commit
		return queue_1;
	}
	public Tranaction_initial(int thread_num,String str_cityw)
	{
		this.thread_num=thread_num;
		this.str_city=str_city;
	}
}







