
require 'rails_helper'

RSpec.describe "Auth::Registrations", type: :request do

  describe "POST /auth" do
    let(:body) { attributes_for(:user) }

    context 'when email and password are valid' do
      it 'responds with 200' do
        post user_registration_path, { params: body }
        expect(response).to have_http_status(200)
      end

      it 'responds with access token in headers' do
        post user_registration_path, { params: body }
        expect(response.headers['access-token']).to be_present
      end

      it 'responds with token type in headers' do
        post user_registration_path, { params: body }
        expect(response.headers['token-type']).to be_present
      end

      it 'responds with client in headers' do
        post user_registration_path, { params: body }
        expect(response.headers['client']).to be_present
      end

      it 'responds with uid in headers' do
        post user_registration_path, { params: body }
        expect(response.headers['uid']).to be_present
      end

      it 'responds with user in body' do
        post user_registration_path, { params: body }
        user = JSON.parse(response.body)
        expect(user['id']).to be_present
        expect(user['name']).to eq body[:name]
        expect(user['email']).to eq body[:email]
      end
    end

    context 'when email is not present' do
      it 'returns a 422' do
        body[:email] = nil
        post user_registration_path, { params: body }
        expect(response).to have_http_status(422)
      end
    end

    context 'when email is empty' do
      it 'returns a 422' do
        body[:email] = ''
        post user_registration_path, { params: body }
        expect(response).to have_http_status(422)
      end
    end

    context 'when email already exists' do
      let(:existing_user) { create :user }
      it 'returns a 422' do
        body[:email] = existing_user.email
        post user_registration_path, { params: body }
        expect(response).to have_http_status(422)
      end
    end

    context 'when email has invalid format' do
      it 'returns a 422' do
        body[:email] = Faker::Lorem.sentence
        post user_registration_path, { params: body }
        expect(response).to have_http_status(422)
      end
    end

    context 'when name is not present' do
      it 'returns a 422' do
        body[:name] = nil
        post user_registration_path, { params: body }
        expect(response).to have_http_status(422)
      end
    end

    context 'when name is empty' do
      it 'returns a 422' do
        body[:name] = ''
        post user_registration_path, { params: body }
        expect(response).to have_http_status(422)
      end
    end

    context 'when password is not present' do
      it 'returns a 422' do
        body[:password] = nil
        post user_registration_path, { params: body }
        expect(response).to have_http_status(422)
      end
    end

    context 'when password is empty' do
      it 'returns a 422' do
        body[:password] = ''
        post user_registration_path, { params: body }
        expect(response).to have_http_status(422)
      end
    end

    context 'when password has less than 8 characters' do
      it 'returns a 422' do
        body[:password] = Faker::Internet.password(1, 7)
        post user_registration_path, { params: body }
        expect(response).to have_http_status(422)
      end
    end

    context 'when password has exactly 8 characters' do
      it 'creates the user' do
        body[:password] = Faker::Internet.password(8, 8)
        post user_registration_path, { params: body }
        expect(response).to have_http_status(200)
      end
    end

    context 'when password has more than 8 characters' do
      it 'creates the user' do
        body[:password] = Faker::Internet.password(9)
        post user_registration_path, { params: body }
        expect(response).to have_http_status(200)
      end
    end
  end

  describe "PATCH /auth" do
    let(:existing_user) { create(:user) }
    let(:valid_session) { existing_user.create_new_auth_token }

    context 'when email is empty' do
      it 'returns a 422' do
        body = { email: '' }
        patch user_registration_path, { params: body, headers: valid_session }
        expect(response).to have_http_status(422)
      end
    end

    context 'when email already exists' do
      let(:another_user) { create :user }
      it 'returns a 422' do
        body = { email: another_user.email }
        patch user_registration_path, { params: body, headers: valid_session }
        expect(response).to have_http_status(422)
      end
    end

    context 'when email has invalid format' do
      it 'returns a 422' do
        body = { email: Faker::Lorem.sentence }
        patch user_registration_path, { params: body, headers: valid_session }
        expect(response).to have_http_status(422)
      end
    end

    context 'when name is empty' do
      it 'returns a 422' do
        body = { name: '' }
        patch user_registration_path, { params: body, headers: valid_session }
        expect(response).to have_http_status(422)
      end
    end

    context 'when password is empty' do
      it 'returns a 422' do
        body = { password: '' }
        patch user_registration_path, { params: body, headers: valid_session }
        expect(response).to have_http_status(422)
      end
    end

    context 'when password has less than 8 characters' do
      it 'returns a 422' do
        body = { password: Faker::Internet.password(1, 7) }
        patch user_registration_path, { params: body, headers: valid_session }
        expect(response).to have_http_status(422)
      end
    end

    context 'when password has exactly 8 characters' do
      it 'updated the user' do
        body = { password: Faker::Internet.password(8, 8) }
        patch user_registration_path, { params: body, headers: valid_session }
        expect(response).to have_http_status(200)
      end
    end

    context 'when password has more than 8 characters' do
      it 'updates the user' do
        body = { password: Faker::Internet.password(9) }
        patch user_registration_path, { params: body, headers: valid_session }
        expect(response).to have_http_status(200)
      end
    end
  end
end
