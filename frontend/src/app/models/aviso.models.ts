export interface Aviso {
  id: string;
  idEvento: string;
  nomeEvento: string;
  idCidade: string;
  nomeCidade: string;
  dataGeracao: string;
  dataReferencia: string;
  valor: number;
  unidadeMedida: string;
}

export interface EnvioLog {
  id: string;
  idAviso: string;
  idCanal: string;
  nomeCanal: string;
  idStatus: string;
  nomeStatus: string;
  idUsuarioDestinatario: string;
  nomeUsuarioDestinatario: string;
  emailUsuarioDestinatario: string;
  whatsappUsuarioDestinatario: string;
  idEvento: string;
  nomeEvento: string;
  idCidade: string;
  nomeCidade: string;
  dataGeracaoAviso: string;
  dataReferenciaAviso: string;
}

export interface ApplicationLogEntry {
  id: string;
  task: string;
  executionId: string;
  level: 'info' | 'warn' | 'error';
  message: string;
  status: string | null;
  extra: string | null;
  createdAt: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  pageNumber: number;
  pageSize: number;
}
