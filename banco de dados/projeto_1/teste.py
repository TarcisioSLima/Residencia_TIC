turno = {
    "M": "Matutino",
    "N": "Noturno",
    "V": "Vespertino"
}
dia = {
    "2": "Segunda",
    "3": "Terça",
    "4": "Quarta",
    "5": "Quinta",
    "6": "Sexta",
    "7": "Sábado"
}
horario_m = {
    "12": "07:10 - 08:50",
    "23": "08:00 - 09:40",
    "34": "08:50 - 10:50",
    "45": "10:00 - 11:40",
    "56": "10:50 - 12:30"
}
horario_v = {
    "12": "13:10 - 14:50",
    "23": "14:00 - 15:40",
    "34": "14:50 - 16:50",
    "45": "16:00 - 17:40",
    "56": "16:50 - 18:30"
}
horario_n = {
    "12": "18:05 - 19:35",
    "23": "18:50 - 20:20",
    "34": "19:35 - 21:15",
    "45": "20:30 - 22:00"
}
h_t = {
    "Matutino": horario_m,
    "Noturno": horario_n,
    "Vespertino": horario_v
}
def esconde_diretorio():
    print()
    print()
    print()
    print()
    print()
    print()
    print()
    print()
    print()

d_tratado = []
t_tratado = []
h_tratado = []

esconde_diretorio()

aulas = str(input("Digite o horário: "))
programa = "active"
i = 0
while programa != "erro":
    dias = dia.get(aulas[i], "erro")
    d_tratado.append(dias)
    programa = dias
    i += 1
i -= 1
d_tratado.pop()

tratamento_t = turno.get(aulas[i], "digitaram errado")
t_tratado.append(tratamento_t)

t_h_t = h_t.get(tratamento_t, "erro no horário turno")

i+=1
horario = t_h_t.get(aulas[-2]+aulas[-1])
h_tratado.append(horario)

print("Turno: " + t_tratado[0] + ". ")
print("Dias da Semana: " + " - ".join(d_tratado) + ". ")
print("Horário: " + h_tratado[0])













