import dayjs from 'dayjs';
import { IUserLogin } from 'app/shared/model/user-login.model';

export interface IAlarm {
  id?: string;
  time?: dayjs.Dayjs | null;
  alarmTime?: dayjs.Dayjs | null;
  enabled?: boolean | null;
  sound?: string | null;
  label?: string | null;
  repeatDays?: dayjs.Dayjs | null;
  snoozeEnabled?: boolean | null;
  snoozeDuration?: number | null;
  userLogin?: IUserLogin | null;
}

export const defaultValue: Readonly<IAlarm> = {
  enabled: false,
  snoozeEnabled: false,
};
