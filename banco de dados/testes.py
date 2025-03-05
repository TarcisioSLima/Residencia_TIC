'''
recomendavel executar a cada ação concluida.

conexao.commit()
cursor.close()
conexao.close()
'''


import mysql.connector

conexao = mysql.connector.connect(
host = "localhost",
user = "root",
password = "321",
database = "db_testes"
)

cursor = conexao.cursor()

tabelas = ["CREATE TABLE tb_funcionario ", 
           "CREATE TABLE tb_cliente "]

atributo_func = [   "(",
                    "id_func INT AUTO_INCREMENT PRIMARY KEY,",
                    "cpf INT(11) UNIQUE NOT NULL,",
                    "nome VARCHAR(50) NOT NULL,",
                    "hora_entrada TIME,",
                    "hora_saida TIME",
                    ");"
                ]

atributo_clie = [   "(",
                    "id_cliente INT AUTO_INCREMENT PRIMARY KEY,",
                    "nome VARCHAR(50) NOT NULL,",
                    "endereco VARCHAR(100)",
                    ");"
                ]

tabelas[0] += " ".join(atributo_func)
tabelas[1] += " ".join(atributo_clie)

for tabela in tabelas:
    cursor.execute(tabela)

conexao.commit()
cursor.close()
conexao.close()

'''
para "simplificar", (porque não está nada simplificado kkkk)
poderia também ser feito em uma matriz os atrbutos, dessa forma,
em um ambiente com muitas tabelas poderiam ser enseridos seus atributos a seus respectivos posição no for,
mas, vou deixar assim mesmo porque é mais leigo e facil de interpretar.
segue o exemplo do código em matrizes



atributos = [

                [   "(",
                    "id_func INT AUTO_INCREMENT PRIMARY KEY,",
                    "cpf INT(11) UNIQUE NOT NULL,",
                    "nome VARCHAR(50) NOT NULL,",
                    "hora_entrada TIME,",
                    "hora_saida TIME",
                    ");"
                ],

                [   "(",
                    "id_cliente INT AUTO_INCREMENT PRIMARY KEY,",
                    "nome VARCHAR(50) NOT NULL,",
                    "endereco VARCHAR(100)",
                    ");"
                ]
            ]

for indice, _ in enumerate(tabelas):
    tabelas[indice] += " ".join(atributos[indice])  
    cursor.execute(tabelas[indice])
conexao.commit()
cursor.close()
conexao.close()
'''
