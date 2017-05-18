require 'rails_helper'

RSpec.describe User, type: :model do
  it 'has a valid factory' do
    expect(build(:user)).to be_valid
  end

  describe 'name' do
    context 'when nil' do
      it 'is invalid' do
        expect(build(:user, name: nil)).not_to be_valid
      end
    end

    context 'when empty' do
      it 'is invalid' do
        expect(build(:user, name: '')).not_to be_valid
      end
    end
  end

  describe 'email' do
    context 'when nil' do
      it 'is invalid' do
        expect(build(:user, email: nil)).not_to be_valid
      end
    end

    context 'when empty' do
      it 'is invalid' do
        expect(build(:user, email: '')).not_to be_valid
      end
    end

    context 'when not containing @' do
      it 'is invalid' do
        expect(build(:user, email: 'hellomail.com')).not_to be_valid
      end
    end

    context 'when not containing domain' do
      it 'is invalid' do
        expect(build(:user, email: 'hello@mail')).not_to be_valid
      end
    end

    context 'when duplicated' do
      let(:existing_user) { create(:user) }
      it 'raises an ActiveRecord::RecordInvalid exception' do
        expect do
          create(:user, email: existing_user.email)
        end.to raise_error ActiveRecord::RecordInvalid
      end
    end
  end

  describe 'password' do
    context 'when nil' do
      it 'is invalid' do
        expect(build(:user, password: nil)).not_to be_valid
      end
    end

    context 'when empty' do
      it 'is invalid' do
        expect(build(:user, password: '')).not_to be_valid
      end
    end

    context 'when has less than 8 characters' do
      it 'is invalid' do
        password_length = Faker::Number.between(1, 7)
        expect(build(:user, password: Faker::Lorem.characters(password_length))).not_to be_valid
      end
    end

    context 'when has exactly 8 characters' do
      it 'is valid' do
        expect(build(:user, password: Faker::Lorem.characters(8))).to be_valid
      end
    end

    context 'when has more than 8 characters' do
      it 'is valid' do
        password_length = Faker::Number.between(9, 30)
        expect(build(:user, password: Faker::Lorem.characters(password_length))).to be_valid
      end
    end
  end

  describe 'role' do
    context 'when nil' do
      it 'is invalid' do
        expect(build(:user, role: nil)).not_to be_valid
      end
    end

    context 'when empty' do
      it 'is invalid' do
        expect(build(:user, role: '')).not_to be_valid
      end
    end

    context "when is 'regular'" do
      it 'is valid' do
        expect(build(:user, role: 'regular')).to be_valid
      end
    end

    context "when is 'manager'" do
      it 'is valid' do
        expect(build(:manager_user)).to be_valid
      end
    end

    context "when is 'admin'" do
      it 'is valid' do
        expect(build(:admin_user)).to be_valid
      end
    end

    context "when not one of predefined roles" do
      it 'is invalid' do
        expect(build(:user, role: Faker::Lorem.characters(10))).not_to be_valid
      end
    end
  end
end
