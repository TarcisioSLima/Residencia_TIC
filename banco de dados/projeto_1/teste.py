def tratamento(valor):
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
        "1": "07:10 - 08:00",
        "2": "08:00 - 08:50",
        "3": "08:50 - 09:40",
        "4": "10:00 - 10:50",
        "5": "10:50 - 11:40",
        "6": "11:40 - 12:30",
        "12": "07:10 - 08:50",
        "23": "08:00 - 09:40",
        "34": "08:50 - 10:50",
        "45": "10:00 - 11:40",
        "56": "10:50 - 12:30"
    }
    horario_v = {
        "1": "13:10 - 14:00",
        "2": "14:00 - 14:50",
        "3": "14:50 - 15:40",
        "4": "16:00 - 16:50",
        "5": "16:50 - 17:40",
        "6": "17:40 - 18:30",
        "12": "13:10 - 14:50",
        "23": "14:00 - 15:40",
        "34": "14:50 - 16:50",
        "45": "16:00 - 17:40",
        "56": "16:50 - 18:30"
    }
    horario_n = {
        "1": "18:05 - 18:50",
        "2": "18:50 - 19:35",
        "3": "19:35 - 20:20",
        "4": "20:30 - 21:15",
        "5": "21:15 - 22:00",
        "12": "18:05 - 19:35",
        "23": "18:50 - 20:20",
        "34": "19:35 - 21:15",
        "45": "20:30 - 22:00"
    }


    return turno.get(valor, "erro")

dias = {
    "2": "Segunda",
    "3": "Terça",
    "4": "Quarta",
    "5": "Quinta",
    "6": "Sexta",
    "7": "Sábado"
}

print(dias.get("2", "turno"))












#
#horario = str(input("Digite o horário: "))
#
#for i in range (len(horario)):
#    print(horario[i])