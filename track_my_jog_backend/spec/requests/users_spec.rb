require 'rails_helper'

RSpec.describe "Users", type: :request do

  describe 'GET /users' do
    context 'as regular user' do
      let(:user) { create(:user) }
      let(:session) { user.create_new_auth_token }

      it 'responds with 403' do
        get users_path, { headers: session }
        expect(response).to have_http_status(403)
      end
    end

    context 'as manager user' do
      let(:user) { create(:manager_user) }
      let(:session) { user.create_new_auth_token }

      it 'responds with 200' do
        get users_path, { headers: session }
        expect(response).to have_http_status(200)
      end

      context 'when there is no users besides him' do
        it 'responds with an array with only his information' do
          get users_path, { headers: session }
          users = JSON.parse(response.body)
          expect(users.count).to eq 1
          expect(users.first['name']).to eq user.name
          expect(users.first['email']).to eq user.email
        end
      end

      context 'when there are more users' do
        it 'responds with all users' do
          user_ids = [user.id] # Include myself first
          Faker::Number.between(1, 10).times { user_ids << create(:user).id }
          Faker::Number.between(1, 10).times { user_ids << create(:manager_user).id }
          Faker::Number.between(1, 10).times { user_ids << create(:admin_user).id }
          get users_path, { headers: session }
          users = JSON.parse(response.body)
          expect(users.count).to eq user_ids.count
          users.each { |u| expect(user_ids).to include(u['id']) }
        end
      end
    end

    context 'as admin user' do
      let(:user) { create(:admin_user) }
      let(:session) { user.create_new_auth_token }

      it 'responds with 200' do
        get users_path, { headers: session }
        expect(response).to have_http_status(200)
      end

      context 'when there is no users besides him' do
        it 'responds with an array with only his information' do
          get users_path, { headers: session }
          users = JSON.parse(response.body)
          expect(users.count).to eq 1
          expect(users.first['name']).to eq user.name
          expect(users.first['email']).to eq user.email
        end
      end

      context 'when there are more users' do
        it 'responds with all users' do
          user_ids = [user.id] # Include myself first
          Faker::Number.between(1, 10).times { user_ids << create(:user).id }
          Faker::Number.between(1, 10).times { user_ids << create(:manager_user).id }
          Faker::Number.between(1, 10).times { user_ids << create(:admin_user).id }
          get users_path, { headers: session }
          users = JSON.parse(response.body)
          expect(users.count).to eq user_ids.count
          users.each { |u| expect(user_ids).to include(u['id']) }
        end
      end
    end
  end

  describe 'GET /users/:id' do
    context 'when requested user does not exist' do
      it 'raises ActiveRecord::RecordNotFound exception' do
        expect do
          get user_path(Faker::Number.between(9999, 99999)), {
            headers: create(:manager_user).create_new_auth_token
          }
        end.to raise_error ActiveRecord::RecordNotFound
      end
    end

    context 'as regular user' do
      let(:user) { create(:user) }
      let(:session) { user.create_new_auth_token }

      it 'responds with 403' do
        get user_path(user.id), { headers: session }
        expect(response).to have_http_status(403)
      end
    end

    context 'as manager user' do
      let(:user) { create(:manager_user) }
      let(:session) { user.create_new_auth_token }

      before do
        @other_user = create(:user)
      end

      it 'responds with 200' do
        get user_path(@other_user.id), { headers: session }
        expect(response.status).to eq(200)
      end

      it 'responds with the requested user information' do
        get user_path(@other_user.id), { headers: session }
        other_user = JSON.parse(response.body)
        expect(other_user['id']).to eq(@other_user.id)
        expect(other_user['name']).to eq(@other_user.name)
        expect(other_user['email']).to eq(@other_user.email)
      end
    end

    context 'as admin user' do
      let(:user) { create(:admin_user) }
      let(:session) { user.create_new_auth_token }

      before do
        @other_user = create(:user)
      end

      it 'responds with 200' do
        get user_path(@other_user.id), { headers: session }
        expect(response.status).to eq(200)
      end

      it 'responds with the requested user information' do
        get user_path(@other_user.id), { headers: session }
        other_user = JSON.parse(response.body)
        expect(other_user['id']).to eq(@other_user.id)
        expect(other_user['name']).to eq(@other_user.name)
        expect(other_user['email']).to eq(@other_user.email)
      end
    end
  end

  describe 'POST /users' do
    context 'as regular user' do
      let(:user) { create(:user) }
      let(:session) { user.create_new_auth_token }

      it 'responds with 403' do
        post users_path, { params: body, headers: session }
        expect(response).to have_http_status(403)
      end
    end

    context 'when logged in with enough permissions' do
      let(:body) { attributes_for :user }
      let(:session) { create(:manager_user).create_new_auth_token }

      context 'when attributes are valid' do
        it 'responds with 200' do
          post users_path, { params: body, headers: session }
          expect(response).to have_http_status(200)
        end

        it 'responds with user in body' do
          post users_path, { params: body, headers: session }
          user = JSON.parse(response.body)
          expect(user['id']).to be_present
          expect(user['name']).to eq body[:name]
          expect(user['email']).to eq body[:email]
        end
      end

      context 'when email is not present' do
        it 'returns a 422' do
          body[:email] = nil
          post users_path, { params: body, headers: session }
          expect(response).to have_http_status(422)
        end
      end

      context 'when email is empty' do
        it 'returns a 422' do
          body[:email] = ''
          post users_path, { params: body, headers: session }
          expect(response).to have_http_status(422)
        end
      end

      context 'when email already exists' do
        let(:existing_user) { create :user }
        it 'returns a 422' do
          body[:email] = existing_user.email
          post users_path, { params: body, headers: session }
          expect(response).to have_http_status(422)
        end
      end

      context 'when email has invalid format' do
        it 'returns a 422' do
          body[:email] = Faker::Lorem.sentence
          post users_path, { params: body, headers: session }
          expect(response).to have_http_status(422)
        end
      end

      context 'when name is not present' do
        it 'returns a 422' do
          body[:name] = nil
          post users_path, { params: body, headers: session }
          expect(response).to have_http_status(422)
        end
      end

      context 'when name is empty' do
        it 'returns a 422' do
          body[:name] = ''
          post users_path, { params: body, headers: session }
          expect(response).to have_http_status(422)
        end
      end

      context 'when password is not present' do
        it 'returns a 422' do
          body[:password] = nil
          post users_path, { params: body, headers: session }
          expect(response).to have_http_status(422)
        end
      end

      context 'when password is empty' do
        it 'returns a 422' do
          body[:password] = ''
          post users_path, { params: body, headers: session }
          expect(response).to have_http_status(422)
        end
      end

      context 'when password has less than 8 characters' do
        it 'returns a 422' do
          body[:password] = Faker::Internet.password(1, 7)
          post users_path, { params: body, headers: session }
          expect(response).to have_http_status(422)
        end
      end

      context 'when password has exactly 8 characters' do
        it 'creates the user' do
          body[:password] = Faker::Internet.password(8, 8)
          post users_path, { params: body, headers: session }
          expect(response).to have_http_status(200)
        end
      end

      context 'when password has more than 8 characters' do
        it 'creates the user' do
          body[:password] = Faker::Internet.password(9)
          post users_path, { params: body, headers: session }
          expect(response).to have_http_status(200)
        end
      end

      context 'as manager user' do
        let(:manager_session) { create(:manager_user).create_new_auth_token }

        context 'when role is regular' do
          it 'creates the user' do
            body[:role] = :regular
            post users_path, { params: body, headers: manager_session }
            expect(response).to have_http_status(200)
            expect(JSON.parse(response.body)['role'].to_sym).to eq :regular
          end
        end

        context 'when role is manager' do
          it 'creates the user' do
            body[:role] = :manager
            post users_path, { params: body, headers: manager_session }
            expect(response).to have_http_status(200)
            expect(JSON.parse(response.body)['role'].to_sym).to eq :manager
          end
        end

        context 'when role is admin' do
          it 'responds with 403' do
            body[:role] = :admin
            post users_path, { params: body, headers: manager_session }
            expect(response).to have_http_status(403)
          end
        end
      end

      context 'as admin user' do
        let(:admin_session) { create(:admin_user).create_new_auth_token }

        context 'when role is regular' do
          it 'creates the user' do
            body[:role] = :regular
            post users_path, { params: body, headers: admin_session }
            expect(response).to have_http_status(200)
            expect(JSON.parse(response.body)['role'].to_sym).to eq :regular
          end
        end

        context 'when role is manager' do
          it 'creates the user' do
            body[:role] = :manager
            post users_path, { params: body, headers: admin_session }
            expect(response).to have_http_status(200)
            expect(JSON.parse(response.body)['role'].to_sym).to eq :manager
          end
        end

        context 'when role is admin' do
          it 'creates the user' do
            body[:role] = :admin
            post users_path, { params: body, headers: admin_session }
            expect(response).to have_http_status(200)
            expect(JSON.parse(response.body)['role'].to_sym).to eq :admin
          end
        end
      end
    end
  end

  describe 'PATCH /users/:id' do
    context 'as regular user' do
      let(:user) { create(:user) }
      let(:session) { user.create_new_auth_token }

      it 'responds with 403' do
        patch user_path(user.id), { params: body, headers: session }
        expect(response).to have_http_status(403)
      end
    end

    context 'when logged in with enough permissions' do
      let(:existing_user) { create :user }
      let(:body) { attributes_for(:user) }
      let(:session) { create(:manager_user).create_new_auth_token }

      context 'when requested user does not exist' do
        it 'raises ActiveRecord::RecordNotFound exception' do
          expect do
            patch user_path(Faker::Number.between(9999, 99999)), { headers: session }
          end.to raise_error ActiveRecord::RecordNotFound
        end
      end

      context 'when attributes are valid' do
        it 'responds with 200' do
          patch user_path(existing_user.id), { params: body, headers: session }
          expect(response).to have_http_status(200)
        end

        it 'responds with user in body' do
          patch user_path(existing_user.id), { params: body, headers: session }
          user = JSON.parse(response.body)
          expect(user['id']).to eq existing_user.id
          expect(user['name']).to eq body[:name]
          expect(user['email']).to eq body[:email]
        end
      end

      context 'when email is not present' do
        it 'returns a 422' do
          body[:email] = nil
          patch user_path(existing_user.id), { params: body, headers: session }
          expect(response).to have_http_status(422)
        end
      end

      context 'when email is empty' do
        it 'returns a 422' do
          body[:email] = ''
          patch user_path(existing_user.id), { params: body, headers: session }
          expect(response).to have_http_status(422)
        end
      end

      context 'when email already exists' do
        let(:other_user) { create :user }
        it 'returns a 422' do
          body[:email] = other_user.email
          patch user_path(existing_user.id), { params: body, headers: session }
          expect(response).to have_http_status(422)
        end
      end

      context 'when email has invalid format' do
        it 'returns a 422' do
          body[:email] = Faker::Lorem.sentence
          patch user_path(existing_user.id), { params: body, headers: session }
          expect(response).to have_http_status(422)
        end
      end

      context 'when name is not present' do
        it 'returns a 422' do
          body[:name] = nil
          patch user_path(existing_user.id), { params: body, headers: session }
          expect(response).to have_http_status(422)
        end
      end

      context 'when name is empty' do
        it 'returns a 422' do
          body[:name] = ''
          patch user_path(existing_user.id), { params: body, headers: session }
          expect(response).to have_http_status(422)
        end
      end

      context 'when password has less than 8 characters' do
        it 'returns a 422' do
          body[:password] = Faker::Internet.password(1, 7)
          patch user_path(existing_user.id), { params: body, headers: session }
          expect(response).to have_http_status(422)
        end
      end

      context 'when password has exactly 8 characters' do
        it 'updates the user' do
          body[:password] = Faker::Internet.password(8, 8)
          patch user_path(existing_user.id), { params: body, headers: session }
          expect(response).to have_http_status(200)
        end
      end

      context 'when password has more than 8 characters' do
        it 'updates the user' do
          body[:password] = Faker::Internet.password(9)
          patch user_path(existing_user.id), { params: body, headers: session }
          expect(response).to have_http_status(200)
        end
      end

      context 'as manager user' do
        let(:manager_session) { create(:manager_user).create_new_auth_token }

        context 'when role is regular' do
          it 'updates the user' do
            body[:role] = :regular
            patch user_path(create(:user).id), { params: body, headers: manager_session }
            expect(response).to have_http_status(200)
            expect(JSON.parse(response.body)['role'].to_sym).to eq :regular
          end
        end

        context 'when role is manager' do
          it 'updates the user' do
            body[:role] = :manager
            patch user_path(create(:manager_user).id), { params: body, headers: manager_session }
            expect(response).to have_http_status(200)
            expect(JSON.parse(response.body)['role'].to_sym).to eq :manager
          end
        end

        context 'when role to update is admin' do
          it 'responds with 403' do
            patch user_path(create(:admin_user).id), { params: body, headers: manager_session }
            expect(response).to have_http_status(403)
          end
        end
      end

      context 'as admin user' do
        let(:admin_session) { create(:admin_user).create_new_auth_token }

        context 'when role is regular' do
          it 'updates the user' do
            body[:role] = :regular
            patch user_path(create(:user).id), { params: body, headers: admin_session }
            expect(response).to have_http_status(200)
            expect(JSON.parse(response.body)['role'].to_sym).to eq :regular
          end
        end

        context 'when role is manager' do
          it 'updates the user' do
            body[:role] = :manager
            patch user_path(create(:manager_user).id), { params: body, headers: admin_session }
            expect(response).to have_http_status(200)
            expect(JSON.parse(response.body)['role'].to_sym).to eq :manager
          end
        end

        context 'when role is admin' do
          it 'updates the user' do
            body[:role] = :admin
            patch user_path(create(:admin_user).id), { params: body, headers: admin_session }
            expect(response).to have_http_status(200)
            expect(JSON.parse(response.body)['role'].to_sym).to eq :admin
          end
        end
      end
    end
  end

  describe 'DELETE /users/:id' do
    context 'as regular user' do
      let(:user) { create(:user) }
      let(:session) { user.create_new_auth_token }

      it 'responds with 403' do
        delete user_path(user.id), { params: body, headers: session }
        expect(response).to have_http_status(403)
      end
    end

    context 'when logged in with enough permissions' do
      let(:existing_user) { create :user }
      let(:session) { create(:manager_user).create_new_auth_token }

      context 'when requested user does not exist' do
        it 'raises ActiveRecord::RecordNotFound exception' do
          expect do
            delete user_path(Faker::Number.between(9999, 99999)), { headers: session }
          end.to raise_error ActiveRecord::RecordNotFound
        end
      end

      context 'as manager user' do
        let(:manager_session) { create(:manager_user).create_new_auth_token }

        context 'when role to delete is regular' do
          it 'deletes the user' do
            delete user_path(create(:user).id), { params: body, headers: manager_session }
            expect(response).to have_http_status(200)
          end
        end

        context 'when role to delete is manager' do
          it 'deletes the user' do
            delete user_path(create(:manager_user).id), { params: body, headers: manager_session }
            expect(response).to have_http_status(200)
          end
        end

        context 'when role to delete is admin' do
          it 'responds with 403' do
            delete user_path(create(:admin_user).id), { params: body, headers: manager_session }
            expect(response).to have_http_status(403)
          end
        end
      end

      context 'as admin user' do
        let(:admin_session) { create(:admin_user).create_new_auth_token }

        context 'when role to delete is regular' do
          it 'deletes the user' do
            delete user_path(create(:user).id), { params: body, headers: admin_session }
            expect(response).to have_http_status(200)
          end
        end

        context 'when role to delete is manager' do
          it 'deletes the user' do
            delete user_path(create(:manager_user).id), { params: body, headers: admin_session }
            expect(response).to have_http_status(200)
          end
        end

        context 'when role to delete is admin' do
          it 'responds with 200' do
            delete user_path(create(:admin_user).id), { params: body, headers: admin_session }
            expect(response).to have_http_status(200)
          end
        end
      end
    end
  end
end
