export interface IUserLogin {
  id?: number;
  username?: string;
  password?: string;
}

export const defaultValue: Readonly<IUserLogin> = {};
