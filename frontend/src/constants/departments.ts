export const DEPARTMENTS = [
  'MATH',
  'SCIENCE',
  'COMPUTER_SCIENCE',
  'HISTORY',
  'ART',
  'ECONOMICS',
  'LITERATURE',
  'WRITING_AND_GRAMMAR',
  'LAW',
  'CULTURE',
  'LANGUAGE',
  'GEOGRAPHY',
  'GLOBAL_STUDIES',
] as const

export type Department = (typeof DEPARTMENTS)[number]

export const DEPARTMENT_LABELS: Record<Department, string> = {
  MATH: 'Math',
  SCIENCE: 'Science',
  COMPUTER_SCIENCE: 'Computer Science',
  HISTORY: 'History',
  ART: 'Art',
  ECONOMICS: 'Economics',
  LITERATURE: 'Literature',
  WRITING_AND_GRAMMAR: 'Writing & Grammar',
  LAW: 'Law',
  CULTURE: 'Culture',
  LANGUAGE: 'Language',
  GEOGRAPHY: 'Geography',
  GLOBAL_STUDIES: 'Global Studies',
}
