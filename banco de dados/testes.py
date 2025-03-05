

tb_1 = ["CREATE TABLE tb_testes (id_teste INT AUTO INCREMENT PRIMARY KEY, " ]
#misericórdia, python n deixa quebrar linhas a vontade igual no php, preciso me acostumar :( 
atributos_test = ["ola ", "tudo b", "em?", "iss", "o é um", " teste"]
# ou será que n, muhahhaha
print(*atributos_test)
tb_1[0] += "ola"

print(tb_1)

