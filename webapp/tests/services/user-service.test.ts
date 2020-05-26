import axios from 'axios';
import { generateUser } from "../helpers/generators/dto-generators";
import { getAllUsers } from "../../src/services/user-service"

jest.mock('axios');

const mockedAxios = axios as jest.Mocked<typeof axios>;

describe("User service", () => {

    describe("Get all users", () => {
        it("get all the users", async () => {
            const allUsers = [generateUser(), generateUser()]

            mockedAxios.get.mockResolvedValue(allUsers)
            
            const res = await getAllUsers();
            expect(res).toStrictEqual(allUsers);
        })
    })
})