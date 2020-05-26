import axios from 'axios';
import { User } from "../dto"

export interface UserService {
    getAllUsers: () => Promise<Array<User>>;
}

export const getAllUsers = (): Promise<Array<User>> =>
    axios.get("/api/user")