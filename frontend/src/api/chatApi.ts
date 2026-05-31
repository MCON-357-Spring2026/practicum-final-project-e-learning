import axiosClient from './axiosClient'
import type { Department } from '@/constants/departments'

export type Sender = 'SYSTEM' | 'AGENT' | 'USER'

export interface ChatMessage {
  message: string
  from: Sender
  time: string
}

export interface ChatConversation {
  conversationId: string
  personId: string
  title: string
  subject: Department
  messages: ChatMessage[]
}

export interface ChatPreview {
  conversationId: string
  title: string
  subject: Department
}

export const chatApi = {
  getById(id: string) {
    return axiosClient.get<ChatConversation>(`/chats/${id}`)
  },
  getByPersonId(personId: string) {
    return axiosClient.get<ChatConversation[]>(`/chats/person/${personId}`)
  },
  getPreviewsByPersonId(personId: string) {
    return axiosClient.get<ChatPreview[]>(`/chats/person/${personId}/previews`)
  },
  start(personId: string, payload: { subject: string; userMessage: string; timestamp: string }) {
    return axiosClient.post<ChatConversation>(`/chats/person/${personId}/start`, payload)
  },
  sendMessage(conversationId: string, userMessage: string, timestamp: string) {
    return axiosClient.patch<ChatConversation>(`/chats/${conversationId}/message`, { userMessage, timestamp })
  },
  rename(id: string, title: string) {
    return axiosClient.patch<ChatConversation>(`/chats/${id}/rename`, title)
  },
  delete(id: string) {
    return axiosClient.delete(`/chats/${id}`)
  }
}
