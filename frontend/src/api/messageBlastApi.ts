import axiosClient from './axiosClient'

export interface BasicBlastUser {
  personId: string
  name: string
  email: string | null
}

export interface MessageBlastPreviewData {
  id: string
  subject: string
  body: string
  sentAt: string
  sender: BasicBlastUser | null
  recipients: BasicBlastUser[]
}

export const messageBlastApi = {
  getMine() {
    return axiosClient.get<MessageBlastPreviewData[]>('/message-blasts/')
  }
}
