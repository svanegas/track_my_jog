require 'rails_helper'

RSpec.describe TimeEntry, type: :model do
  it 'has a valid factory' do
    expect(build(:time_entry)).to be_valid
  end

  describe 'date' do
    context 'when nil' do
      it 'is invalid' do
        expect(build(:time_entry, date: nil)).not_to be_valid
      end
    end

    context 'when empty' do
      it 'is invalid' do
        expect(build(:time_entry, date: '')).not_to be_valid
      end
    end

    context 'when is in future' do
      it 'is invalid' do
        expect(build(:time_entry, date: Faker::Date.forward(30))).not_to be_valid
      end
    end

    context 'when is today' do
      it 'is valid' do
        expect(build(:time_entry, date: Date.today)).to be_valid
      end
    end

    context 'when is in the past' do
      it 'is valid' do
        expect(build(:time_entry, date: Faker::Date.backward(30))).to be_valid
      end
    end
  end

  describe 'distance' do
    context 'when nil' do
      it 'is invalid' do
        expect(build(:time_entry, distance: nil)).not_to be_valid
      end
    end

    context 'when empty' do
      it 'is invalid' do
        expect(build(:time_entry, distance: '')).not_to be_valid
      end
    end

    context 'when is negative' do
      it 'is invalid' do
        expect(build(:time_entry, distance: Faker::Number.negative.to_i)).not_to be_valid
      end
    end

    context 'when is zero' do
      it 'is invalid' do
        expect(build(:time_entry, distance: 0)).not_to be_valid
      end
    end

    context 'when is greater than zero' do
      it 'is valid' do
        expect(build(:time_entry, distance: Faker::Number.positive.to_i)).to be_valid
      end
    end
  end

  describe 'duration' do
    context 'when nil' do
      it 'is invalid' do
        expect(build(:time_entry, duration: nil)).not_to be_valid
      end
    end

    context 'when empty' do
      it 'is invalid' do
        expect(build(:time_entry, duration: '')).not_to be_valid
      end
    end

    context 'when is negative' do
      it 'is invalid' do
        expect(build(:time_entry, duration: Faker::Number.negative.to_i)).not_to be_valid
      end
    end

    context 'when is zero' do
      it 'is invalid' do
        expect(build(:time_entry, duration: 0)).not_to be_valid
      end
    end

    context 'when is greater than zero' do
      it 'is valid' do
        expect(build(:time_entry, duration: Faker::Number.positive.to_i)).to be_valid
      end
    end
  end

  describe 'user' do
    context 'when nil' do
      it 'is invalid' do
        expect(build(:time_entry, user: nil)).not_to be_valid
      end
    end
  end
end
