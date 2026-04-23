import axiosClient from './axiosClient'

export const messageApi = {
  getById(id: string) {
    return axiosClient.get(`/messages/${id}`)
  },
  getByUserId(userId: string) {
    return axiosClient.get(`/messages/user/${userId}`)
  },
  getBySenderId(senderId: string) {
    return axiosClient.get(`/messages/sender/${senderId}`)
  },
  getByRecipientId(recipientId: string) {
    return axiosClient.get(`/messages/recipient/${recipientId}`)
  },
  getConversation(senderId: string, recipientId: string) {
    return axiosClient.get(`/messages/conversation`, { params: { senderId, recipientId } })
  },
  send(message: { receiverId: string; subject: string; body: string }) {
    return axiosClient.post(`/messages/`, message)
  },
  markAsRead(id: string) {
    return axiosClient.patch(`/messages/${id}`, { read: true })
  },
  markAsUnread(id: string) {
    return axiosClient.patch(`/messages/${id}`, { read: false })
  },
  delete(id: string) {
    return axiosClient.delete(`/messages/${id}`)
  }
}
