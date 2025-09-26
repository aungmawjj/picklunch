export interface User {
  username: string;
  displayName?: string;
}

export interface LunchOption {
  id: number;
  submitter: User;
  shopName: string;
  shopUrl?: string;
}

export interface LunchPicker {
  id: number;
  state: 'SUBMITTING' | 'READY_TO_PICK' | 'PICKED';
  lunchOptions?: LunchOption[];
  firstSubmitter?: User;
  startTime: string;
  waitTimeEnd: string;
  waitTime: string;
  firstSubmittedUsername: string;
  pickedLunchOption: LunchOption;
}

export interface PagedLunchPickers {
  content: LunchPicker[];
  page: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
  };
}

export interface CreateLunchPickerRequest {
  waitTime?: string;
}

export interface SubmitLunchOptionRequest {
  lunchPickerId: number;
  shopName: string;
  shopUrl?: string;
}

export interface PickLunchOptionRequest {
  lunchPickerId: number;
}
