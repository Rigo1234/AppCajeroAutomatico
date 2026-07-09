<h3>App estudiantil que simula un cajero automatico y sus funciones basicas</h3>

<h2><b>Objetivo Especifico</b></h2>
El obejetivo de la creacion de este proyecto es el poner en practica los conocimientos adquiridos en clase, asi como tambien el incentivar a explorar las diferentes herramientas que se nos ha nproporcionada, para de esta forma lograr crear app que cumplan ademas con suproposito, que sean amigables y agradables al usuario.

<h4>Datos de Prueba</h4>
<table>
  <tr>
    <td>Usuario</td><td>Pin</td><td>Cuenta</td><td>Saldo</td>
  </tr>
  <tr>
    <td>rraudales</td><td>2017</td><td>20160709</td><td>50000</td>
  </tr>
  <tr>
    <td>maria77</td><td>9989</td><td>20160708</td><td>20100</td>
  </tr>
</table>

Nota: Para nuestro proyecto decidimos hacer uso de la interfas de los cajeros del banco Ficohsa.

<h4>Logica de Funcionamiento</h4>
El aplicativo tiene una pantalla de inicio que simula la pantalla prinicpal de espera en los cajeros Ficosha antes de ingresar la tarjeta y funciona de la siguiente manera:

<h4>Paso 1</h4>
El usuario debera presionar cualquier tecla para poder acceder a la pantalla del login.<br>
<img width="500" height="300" alt="Captura de pantalla 2026-07-09 111351" src="https://github.com/user-attachments/assets/9719ab34-4b19-41c9-8724-947c85f21a5d" />


<h4>Paso 2</h4>
El usuario accedera a una pantalla de login mediante la cual debera ingresar su usuario y pin asociado a dicho usuario, la app realiza las respectivas validaciones, que tanto el usuario y pin/contraseñ sean los correctos y que los campos no esten bacios, para realizar dichas validaciones previamente creamos un archivo .txt el cual contiene los datos de usurio que simulara n la base de datos.<br>
<img width="500" height="300" alt="Captura de pantalla 2026-07-09 111424" src="https://github.com/user-attachments/assets/d56dcaa9-c801-4845-bc93-ff1582711c8a" />


<h4>Login, como funciona?</h4>
Se creo previamente un archivo llamado usuarios.txt(ruta en nuestro proyecto: src/main/webapp/resourses/cajero-logs), esta estruturado de la siguiente manera: usuario,pin,cuenta,saldo.
mediante las validaciones del login accedemos a nuestro archivo, el cual recorremos linea por linea, especificamente los primeros 2 campos los cuales contienen el usurio y la contrasena, de no encontrarlo o no coincidir nos mostrara un mensaje de error en la parte inferior de nuestro formulario.
Si encuentra coinicidencias nos dirige hacia el menu de opciones.

<h4>Paso 3</h4>
Una vez pasamos a nuestro menu de opciones asignamos valores a variable previamente creadas, estas serian usuarioActivo, saldoActivo, para poder trabajar con estos valores segun la opcion seleccionada.<br>
<img width="500" height="300" alt="Captura de pantalla 2026-07-09 111449" src="https://github.com/user-attachments/assets/41cfe6d7-75a0-4e72-9b2c-2eaf402cd579" />


<h4>Consulta Saldo</h4>
Al elegir esta opcion se nos mostrara una pantalla enl que e nos mostrar nuestros datos de usuario como ser: Usuario, Cuenta y el Saldo actual en nuestra cuenta, se agrego un boton para regresar a la pantalla principal.<br>
<img width="500" height="300" alt="image" src="https://github.com/user-attachments/assets/fcb8c564-afdb-4281-9b78-2054deed0ee1" />


<h4>Retiro/Deposito</h4>
Si elegimos una e estas 2 opciones seremos dirigigos hacia una pantalla donde tendremos opciones de retiro/deposito ya definidas y tambien una opcion mas donde el usuario podra ingresar la cantidad que desea retitar/depositar el registro de cada movimiento se realiza en un archivo personalizado para cada usurio.<br>
<img width="500" height="300" alt="Captura de pantalla 2026-07-09 111523" src="https://github.com/user-attachments/assets/642a899c-82e4-4aa3-adca-b596aecb3120" />


<h4>Como funciona?</h4>
Si el usurio se selecciona una de las opciones ya definidas por ejemplo "100" al presionar el boton se asigna el valor a una variable asignada al retiro/deposito(montoRetirar/montoDepositar), sitenemos el saldo suficiente se nos permitira realizar la confirmacion de la transaccion, de no ser asi se nos pedira ingresar un onto valido para nuestra transaccion.

Si el usuario selecciona "otro monto" se nos dirigira hacia la misma pantalla de confirmacion y aqui deberemos ingresar el monto que deseamos retirar.
En esta pantalla de confirmacion se nos muestran el usurio y el saldo disponible, de no ingresar un monto validao se nos muestra un mensaje en la parte inferior de la pantalla.

Una vez  validada la operacion se nos dirige hacia una pantalla donde se nos mostra nuestro usuario y nuestro aldo actualizado, despues de la resta/suma segun sea el caso, para actulizar los datos de la cuenta se realiza mediante el metodo actualizarUsuarioTxt() el cual almacena los datos.

<h4>Registro de movimientos en el archivo "usuario1234.txt"</h4>
Una vez realizadas las validaciones del login y elegir una de las 2 opciones transaccionales (retiro/deposito), si confirmamos la transaccion se ejecutara un metodo llamado registarMovimiento() mediante el cual almacenamos de la siguiente manera fechaHora,tipo de transaccion, monto.
Si el archivo no existe el metodo lo crea asignandole el nombre combinando el usuario y la cuenta, seguidamente almacena la transaccion. Si el archivo ya exite solamente agrega la transaccion al final de el archivo.


<h4>Historial</h4>
Si el usuario elige esta opcion se nos diige hacia una pantalla en lo cual se nos muestra el historial de las transacciones realizadas por dicho usuario en caso de no encontrar se nos mostrara un mensaje indicandonos que no tenemos transacciones para dicho usurio.<br>
<img width="500" height="300" alt="image" src="https://github.com/user-attachments/assets/3f78cc69-d440-4a25-91a4-e733056fa897" />

<h4>Como funciona?</h4>
Mediante un metodo llamado getMovimientosPorUsuario recorremos el archivo ocrrespondiente al usaurio seleccionado, extraemo los datos linea por linea y los mostramos en una pantalla llamada historial, en caso de no haber registro de transacciones, se mostrara en pantalla que no hay registro de movimientos para dicho usuario.
Mediante un metodo llamado getMovimientosPorUsuario() recorremos el archivo especifico para ese usuario y lo extraemos linea por linea al llamarlo en nuestro historail.html se nos mostraran todos los datos extraidos de el archivo txt.
