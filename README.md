# ReViN_p1

##Project Description
ReViN is a basic Java ORM. Currently ReViN only works with postgresql.

##Setup
In order to get ReViN to work in your program you must 

1) Clone a copy of ReViN onto your local machine.

```shell
  git clone <this-repo>
  cd ReViN_p1
  mvn install
```

2) Next, place the following insider your projects pom.xml file:

```
<dependency>
	<groupId>com.revature</groupId>
	<artifactId>ReViN_p1</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```

3) Finally, inside your projects structure (src/main/resources/) add an application.properties file.

 ``` 
  url=path/to/database
  username=your-username
  password=your-password
 ```
## Usage  
  ### Annotating classes  
  All classes which represent objects in database must be annotated.
   - #### @Table(name = "table_name")  
      - Indicates that this class is associated with table 'table_name'  
   - #### @Column(name = "column_name")  
      - Indicates that the Annotated field is a column in the table with the name 'column_name'  
   - #### @Column(name = "column_name", pkey=true, isSerial=true)
   	  - Inside each @Column you can declare whether the Column is a primary key, is a serial field
   - #### Some other fields you can check for is:
   	  - canBeNull = true
   	  - unique = true
   - #### If you want to declare a field as a foreign key you MUST use both fkey and fClass on that field
      - fkey = true
      - fClass=class to reference primary key
  
### User API
 - #### public static RevinService getInstance();
 
 	- returns the singleton instance of the RevinService class. 
 	
 	- This is where you start with ReViN ORM
 - #### public boolean addClass(Class<?> clazz)
 
  	- Adds the class to the database, essentially creating a table based on the "clazz" @Table's table_name
  
 - #### public boolean addObject(Class<?> clazz, Object o) 
 
   	- Adds the Object o to the database
 
 - #### public <T> List<T> getList(Class<T> clazz) 
 
 	- Returns a list of the Objects in the clazz table
 
 - #### public <T> T get(Class<T> clazz, int primaryKey)
  	
  	- Returns an object from the clazz table based on the primaryKey
 
 - #### public boolean updateObj(Class<?> clazz, Object o) 
 
 	- Updates an object from the clazz table
 	
 	- Call this after using a setter to change the Object o 
 	- e.g: o.setUsername("newUsername")
 
 - #### public boolean removeObj(Class<?> clazz, Object o)
 	
 	- Removes a specified object from the clazz table
 	
 
  
  
  
  
  