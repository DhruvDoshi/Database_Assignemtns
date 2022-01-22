import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Queue;

public class Transaction_after implements Runnable{
	public static final String dbase = "jdbc:mysql://1122.170.144.248:3306/csci5408_a2";
	public static final String userdb = "try";
	public static final String passworddb = "check123";
	
	public String str_city;
	public static Queue<Integer> queue_1= makenew();
	public int thread_num;
	
	
	
	public static void main(String args[])
	{
		
		Transaction_after ti1=new Transaction_after(1,"thr1 city");
		Transaction_after ti2=new Transaction_after(2,"thr2 city");
		Transaction_after ti3=new Transaction_after(3,"thr3 city");
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
			BufferedWriter buff_writter = new BufferedWriter(new FileWriter("logfile2.txt",true));
			Connection con_connect=DriverManager.getConnection(dbase,userdb,passworddb);
			con_connect.setAutoCommit(false);
			int i=0;
			buff_writter.write("Thread started"+thread_num+"\n");
			buff_writter.close();
			while(!queue_1.isEmpty())
			{
				System.buff_writter.println(queue_1);
				if(!queue_1.contains(thread_num))
					break;
				if(queue_1.peek()!=queue_1.peek().equals(thread_num) && null )
				{
					try{
						if(i==2){
							Statement statmnt1=con_connect.createStatement();
							statmnt1.executeUpdate(do_Transmit());
							buff_writter = new BufferedWriter(new FileWriter("logfile2.txt",true));
							buff_writter.write("thread commit"+thread_num+"\n");
							buff_writter.close();
							con_connect.commit();
							statmnt1.close();
							con_connect.close();
							queue_1.remove();		
							
						}
						else if(i==1){
							buff_writter = new BufferedWriter(new FileWriter("logfile2.txt",true));
							buff_writter.write("thread update "+thread_num+"\n");
							buff_writter.close();
							Statement s=con_connect.createStatement();
							s.executeQuery(writing_Lock());
							PreparedStatement statmnt2=con_connect.prepareStatement(modify_City());
							statmnt2.setString(1, str_city);
							statmnt2.executeUpdate();
							ResultSet rs1=s.executeQuery(give_List());
							while(rs1.next())
							{
								buff_writter = new BufferedWriter(new FileWriter("logfile2.txt",true));
								buff_writter.write(rs1.getString("state")+"\t"+rs1.getString("info"));
								buff_writter.close();
							}
							s.executeQuery(remove_Lock());
							statmnt2.close();
							queue_1.remove();
							
						}
						else if(i==0){
							Statement statmnt3=con_connect.createStatement();
							statmnt3.executeQuery(reading_Lock());
							ResultSet rs=statmnt3.executeQuery(read_Cust());
							buff_writter = new BufferedWriter(new FileWriter("logfile2.txt",true));
							buff_writter.write("Thread reading "+thread_num+"\n");
							buff_writter.close();
							while(rs.next())
							{
								buff_writter = new BufferedWriter(new FileWriter("logfile2.txt",true));
								buff_writter.write(rs.getString("customer_id")+"\t"+
								rs.getString("customer_unique_id")+"\n"+
								rs.getString("customer_zip_code_prefix")+"\n"+
								rs.getString("customer_city")+"\n"+
								rs.getString("customer_state")+"\n");
								buff_writter.close();
							}
							rs.close();
							rs=statmnt3.executeQuery(give_List());
							while(rs.next())
							{
								buff_writter = new BufferedWriter(new FileWriter("logfile2.txt",true));
								buff_writter.write("ProcessList"+rs.getString("state")+"\t"+rs.getString("info"));
								buff_writter.close();
							}
							statmnt3.executeQuery(remove_Lock());
							statmnt3.close();
							queue_1.remove();
						}
						else{
							
							return;
						}
						
					}
					catch (SQLException e) {
						e.printStackTrace();
					}
					i++;
					
				}
			}
			buff_writter.close();
			}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public Transaction_after(int thread_num, String str_city)
	{
		this.thread_num=thread_num;
		this.str_city=str_city;
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
	
	public static String read_Cust()
	{
		return "select * from cust_updated where customer_zip_code_prefix=1151";
		
	}
	public static String modify_City()
	{
		return "update cust_updated set customer_city = ? where  customer_zip_code_prefix=1151";
		
	}
	public static String do_Transmit()
	{
		return "commit";
	}
	public static String give_List()
	{
		return "show processlist";
	}
	public static String writing_Lock()
	{
		return "Lock tables cust_updated write";
	}
	public static String reading_Lock()
	{
		return "Lock tables cust_updated read";
	}
	public static String remove_Lock()
	{
		return "unlock tables";
	}
}